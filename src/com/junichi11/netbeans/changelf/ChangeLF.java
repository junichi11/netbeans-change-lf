package com.junichi11.netbeans.changelf;

import com.junichi11.netbeans.changelf.ui.options.ChangeLFOptions;
import com.junichi11.netbeans.changelf.ui.wizards.ConfirmationPanel;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.editor.BaseDocument;
import org.netbeans.spi.editor.document.OnSaveTask;

/**
 *
 * @author junichi11
 */
public class ChangeLF implements OnSaveTask {

    public static final String LF = "LF"; // NOI18N
    public static final String CR = "CR"; // NOI18N
    public static final String CRLF = "CRLF"; // NOI18N
    private final Document document;
    private static final Map<String, String> LF_KINDS = new HashMap<String, String>();

    static {
        LF_KINDS.put(LF, BaseDocument.LS_LF);
        LF_KINDS.put(CR, BaseDocument.LS_CR);
        LF_KINDS.put(CRLF, BaseDocument.LS_CRLF);
    }

    private ChangeLF(Document document) {
        this.document = document;
    }

    @Override
    public void performTask() {
        ChangeLFOptions options = ChangeLFOptions.getInstance();

        // user setting is enable
        if (options.isEnable()) {
            String ls = (String) document.getProperty(BaseDocument.READ_LINE_SEPARATOR_PROP);
            final String kind = LF_KINDS.get(options.getLfKind());

            // compare to current file line feed code
            if (!ls.equals(kind)) {

                // show dialog
                if (options.useShowDialog()) {
                    String currentLS = toLFKindsKeyName(ls);
                    final String message = "Do you really want to change Line Feed from " + currentLS + " to " + options.getLfKind() + "?";

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

    @MimeRegistration(mimeType = "", service = OnSaveTask.Factory.class, position = 1500)
    public static final class FactoryImpl implements Factory {

        @Override
        public OnSaveTask createTask(Context context) {
            return new ChangeLF(context.getDocument());
        }
    }
}
