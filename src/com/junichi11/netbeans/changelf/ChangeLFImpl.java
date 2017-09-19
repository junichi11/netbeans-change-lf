package com.junichi11.netbeans.changelf;

import com.junichi11.netbeans.changelf.api.ChangeLF;
import com.junichi11.netbeans.changelf.preferences.ChangeLFPreferences;
import com.junichi11.netbeans.changelf.ui.options.ChangeLFCustomizerProvider;
import com.junichi11.netbeans.changelf.ui.options.ChangeLFOptions;
import com.junichi11.netbeans.changelf.ui.wizards.ConfirmationPanel;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.spi.editor.document.OnSaveTask;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.CompositeCategoryProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author junichi11
 */
@ServiceProvider(service = ChangeLF.class)
public class ChangeLFImpl implements OnSaveTask, ChangeLF {

    public static final String LF = "LF"; // NOI18N
    public static final String CR = "CR"; // NOI18N
    public static final String CRLF = "CRLF"; // NOI18N
    private Document document;
    private static final Map<String, String> LF_KINDS = new HashMap<>();
    private boolean isForce = false;
    private boolean useDialog = false;
    private boolean isEnabled = false;
    private String lfKind = ""; // NOI18N
    private static final Logger LOGGER = Logger.getLogger(ChangeLFImpl.class.getName());

    static {
        LF_KINDS.put(LF, BaseDocument.LS_LF);
        LF_KINDS.put(CR, BaseDocument.LS_CR);
        LF_KINDS.put(CRLF, BaseDocument.LS_CRLF);
    }

    public ChangeLFImpl() {
        this(null);
    }

    private ChangeLFImpl(Document document) {
        this.document = document;
    }

    @Override
    public void performTask() {
        ChangeLFOptions options = ChangeLFOptions.getInstance();
        boolean useProject = false;
        if (!isForce) {
            Project project = getProject();

            // project properties
            if (project != null) {
                useProject = ChangeLFPreferences.useProject(project);
                if (useProject) {
                    isEnabled = ChangeLFPreferences.isEnable(project);
                    if (isEnabled) {
                        useDialog = ChangeLFPreferences.showDialog(project);
                        lfKind = LF_KINDS.get(ChangeLFPreferences.getLfKind(project));
                    }
                }
            }

            // global options
            if (!useProject && !isEnabled) {
                isEnabled = options.isEnable();
                useDialog = options.useShowDialog();
                lfKind = LF_KINDS.get(options.getLfKind());
            }
        }

        // user setting is enable
        if (isEnabled || isForce) {
            String ls = (String) document.getProperty(BaseDocument.READ_LINE_SEPARATOR_PROP);
            final String kind = lfKind;

            // compare to current file line feed code
            if (!ls.equals(kind)) {

                // show dialog
                if (useDialog) {
                    String currentLS = toLFKindsKeyName(ls);
                    String changeLS = toLFKindsKeyName(kind);
                    final String message = NbBundle.getMessage(ChangeLFImpl.class, "ConfirmAdjustLF", currentLS, changeLS); // NOI18N

                    // check EDT
                    String name = Thread.currentThread().getName();
                    if (SwingUtilities.isEventDispatchThread() || name.equals("wizard-descriptor-asynchronous-jobs")) { // NOI18N
                        comfirm(message, kind);
                    } else {
                        SwingUtilities.invokeLater(() -> comfirm(message, kind));
                    }
                } else {
                    setLF(kind);
                }
            }
        }
    }

    @Override
    public void runLocked(Runnable run) {
        run.run();
    }

    @Override
    public boolean cancel() {
        return true;
    }

    private Project getProject() {
        Source source = Source.create(document);
        FileObject fileObject = source.getFileObject();
        return FileOwnerQuery.getOwner(fileObject);
    }

    /**
     * Set line feed to property of Document.
     *
     * @param kind Line feed code(LF, CRLF, CR).
     */
    private void setLF(String kind) {
        document.putProperty(BaseDocument.READ_LINE_SEPARATOR_PROP, kind);
    }

    /**
     * Show confirmation dialog.
     *
     * @param message This is displayed on Panel.
     * @param kind Line feed code(LF, CRLF, CR).
     */
    private void comfirm(final String message, final String kind) {
        ConfirmationPanel confirmationPanel = new ConfirmationPanel("Confirmation", message);
        if (confirmationPanel.showDialog()) {
            setLF(kind);
        }
    }

    /**
     * Get LF kinds key from value.
     *
     * @param value
     * @return
     */
    private String toLFKindsKeyName(String value) {
        for (Entry entry : LF_KINDS.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey().toString();
            }
        }
        return null;
    }

    @Override
    public void change(Document doc) {
        change(doc, null, useDialog);
    }

    @Override
    public void change(Document doc, TYPE type, boolean useDialog) {
        if (doc == null) {
            LOGGER.log(Level.WARNING, "Document is null!"); // NOI18N
            return;
        }
        document = doc;
        if (type != null) {
            isForce = true;
            this.useDialog = useDialog;
            switch (type) {
                case LF:
                    lfKind = LF_KINDS.get(LF);
                    break;
                case CR:
                    lfKind = LF_KINDS.get(CR);
                    break;
                case CRLF:
                    lfKind = LF_KINDS.get(CRLF);
                    break;
                default:
                    throw new AssertionError();
            }
        }
        performTask();
    }

    @Override
    public TYPE getCurrentLineFeedCode(Project project) {
        String lfName = null;
        // project properties
        if (project != null) {
            if (ChangeLFPreferences.useProject(project)) {
                if (ChangeLFPreferences.isEnable(project)) {
                    lfName = ChangeLFPreferences.getLfKind(project);
                }
            }
        }
        if (lfName != null && !lfName.isEmpty()) {
            return toType(lfName);
        }

        // global
        ChangeLFOptions options = ChangeLFOptions.getInstance();

        // only user checks "enable"
        if (options.isEnable()) {
            return toType(options.getLfKind());
        }
        return null;
    }

    @Override
    public CompositeCategoryProvider getCompositCategoryProvider() {
        return ChangeLFCustomizerProvider.createChangeLF();
    }

    /**
     * Convert to TYPE from line feed code name.
     *
     * @param lfName LF | CR | CRLF
     * @return TYPE
     */
    public static TYPE toType(String lfName) {
        if (null == lfName) {
            return null;
        }
        switch (lfName) {
            case LF:
                return TYPE.LF;
            case CR:
                return TYPE.CR;
            case CRLF:
                return TYPE.CRLF;
            default:
                return null;
        }
    }

    public static String fromType(TYPE lfName) {
        if (lfName == null) {
            return null;
        }

        switch (lfName) {
            case LF:
                return LF;
            case CRLF:
                return CRLF;
            case CR:
                return CR;
            default:
                throw new AssertionError(lfName.name());
        }
    }

    @MimeRegistration(mimeType = "", service = OnSaveTask.Factory.class, position = 1500)
    public static final class FactoryImpl implements Factory {

        @Override
        public OnSaveTask createTask(Context context) {
            return new ChangeLFImpl(context.getDocument());
        }
    }
}
