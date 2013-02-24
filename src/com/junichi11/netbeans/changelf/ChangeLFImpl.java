package com.junichi11.netbeans.changelf;

import com.junichi11.netbeans.changelf.api.ChangeLF;
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
import org.netbeans.editor.BaseDocument;
import org.netbeans.spi.editor.document.OnSaveTask;
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
    private static final Map<String, String> LF_KINDS = new HashMap<String, String>();
    private boolean isForce = false;
    private boolean useDialog = false;
    private String lfKind = "";
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
        if (!isForce) {
            useDialog = options.useShowDialog();
            lfKind = LF_KINDS.get(options.getLfKind());
        }

        // user setting is enable
        if (options.isEnable() || isForce) {
            String ls = (String) document.getProperty(BaseDocument.READ_LINE_SEPARATOR_PROP);
            final String kind = lfKind;

            // compare to current file line feed code
            if (!ls.equals(kind)) {

                // show dialog
                if (useDialog) {
                    String currentLS = toLFKindsKeyName(ls);
                    String changeLS = toLFKindsKeyName(kind);
                    final String message = "Do you really want to change Line Feed from " + currentLS + " to " + changeLS + "?";

                    // check EDT
                    if (SwingUtilities.isEventDispatchThread()) {
                        comfirm(message, kind);
                    } else {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                comfirm(message, kind);
                            }
                        });
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
            LOGGER.log(Level.WARNING, "Document is null!");
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

    @MimeRegistration(mimeType = "", service = OnSaveTask.Factory.class, position = 1500)
    public static final class FactoryImpl implements Factory {

        @Override
        public OnSaveTask createTask(Context context) {
            return new ChangeLFImpl(context.getDocument());
        }
    }
}