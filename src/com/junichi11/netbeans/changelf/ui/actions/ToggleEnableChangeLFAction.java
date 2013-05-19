/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package com.junichi11.netbeans.changelf.ui.actions;

import com.junichi11.netbeans.changelf.ChangeLFImpl;
import com.junichi11.netbeans.changelf.preferences.ChangeLFPreferences;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.util.actions.BooleanStateAction;

@ActionID(
        category = "File",
        id = "com.junichi11.netbeans.changelf.ui.actions.ToggleEnableChangeLFAction")
@ActionRegistration(
        lazy = false,
        displayName = "#CTL_ToggleEnableChangeLFAction")
@ActionReferences({
    @ActionReference(path = "Toolbars/File", position = 500),
    @ActionReference(path = "Shortcuts", name = "DS-L")
})
@Messages("CTL_ToggleEnableChangeLFAction=enable/disable line endings")
public final class ToggleEnableChangeLFAction extends BooleanStateAction {

    private static final long serialVersionUID = -538737826355249808L;
    private static final Logger LOGGER = Logger.getLogger(ToggleEnableChangeLFAction.class.getName());
    private static final String LF_ICON_16 = "com/junichi11/netbeans/changelf/resources/lf_16.png"; // NOI18N
    private static final String CRLF_ICON_16 = "com/junichi11/netbeans/changelf/resources/crlf_16.png"; // NOI18N
    private static final String CR_ICON_16 = "com/junichi11/netbeans/changelf/resources/cr_16.png"; // NOI18N
    private static final ToggleEnableChangeLFAction INSTANCE = new ToggleEnableChangeLFAction();
    private Lookup.Result result;
    private SettingState settingState;

    private ToggleEnableChangeLFAction() {
        result = Utilities.actionsGlobalContext().lookupResult(FileObject.class);
        result.addLookupListener(new LookupListenerImpl());
        settingState = GlobalSettingState.getInstance();
    }

    public static ToggleEnableChangeLFAction getInstance() {
        return INSTANCE;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean enable = settingState.isEnable();
        settingState.setEnable(!enable);
        setBooleanState(!enable);
    }

    @Override
    public String getName() {
        return Bundle.CTL_ToggleEnableChangeLFAction();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected String iconResource() {
        return getIconResource(settingState.getLfKind());
    }

    @Override
    protected void initialize() {
        super.initialize();
        setBooleanState(settingState.isEnable());
    }

    /**
     * Set icon for LF kind.
     */
    public void setIcon(String lfKind) {
        String icon = getIconResource(lfKind);
        if (icon != null) {
            setIcon(ImageUtilities.loadImageIcon(icon, true));
        }
    }

    /**
     * Set icon according to the state.
     */
    private void setIcon() {
        String lfKind = settingState.getLfKind();
        setIcon(lfKind);
    }

    /**
     * Get icon resource for LF kind.
     *
     * @param lfKind
     * @return icon resource
     */
    @NbBundle.Messages("LBL_NotFoundIconResource=Not found icon resource")
    public String getIconResource(String lfKind) {
        String icon = null; // NOI18N
        if (lfKind.equals(ChangeLFImpl.CR)) {
            icon = CR_ICON_16;
        } else if (lfKind.equals(ChangeLFImpl.CRLF)) {
            icon = CRLF_ICON_16;
        } else if (lfKind.equals(ChangeLFImpl.LF)) {
            icon = LF_ICON_16;
        } else {
            LOGGER.log(Level.WARNING, Bundle.LBL_NotFoundIconResource());
        }
        return icon;
    }

    /**
     * Change state to project or global.
     *
     * @param project
     */
    public void changeState(Project project) {
        boolean useGlobal = true;
        if (project != null) {
            useGlobal = ChangeLFPreferences.useGlobal(project);
        }
        if (useGlobal) {
            settingState = GlobalSettingState.getInstance();
        } else {
            settingState = ProjectSettingState.getInstance(project);
        }
        setIcon();
        setBooleanState(settingState.isEnable());
    }

    //~ inner class
    private class LookupListenerImpl implements LookupListener {

        private Project project;

        public LookupListenerImpl() {
        }

        @Override
        public void resultChanged(LookupEvent lookupEvent) {
            FileObject fo = getFileObject(lookupEvent);
            Project tmpProject = null;
            if (fo != null) {
                tmpProject = FileOwnerQuery.getOwner(fo);
            }
            if (tmpProject == null) {
                tmpProject = getProject();
            }
            if (tmpProject == null || project == tmpProject) {
                return;
            }
            project = tmpProject;

            changeState(project);
        }

        /**
         * Get FileObject
         *
         * @param lookupEvent
         * @return current FileObject if exists, otherwise null
         */
        private FileObject getFileObject(LookupEvent lookupEvent) {
            Lookup.Result lookupResult = (Lookup.Result) lookupEvent.getSource();
            Collection c = lookupResult.allInstances();
            FileObject fileObject = null;
            if (!c.isEmpty()) {
                fileObject = (FileObject) c.iterator().next();
            }
            return fileObject;
        }

        /**
         * Get Project
         *
         * @return current Project if exists, otherwise null
         */
        private Project getProject() {
            Lookup context = Utilities.actionsGlobalContext();
            Project prj = context.lookup(Project.class);
            if (prj == null) {
                Node node = context.lookup(Node.class);
                if (node != null) {
                    DataObject dataObject = node.getLookup().lookup(DataObject.class);
                    if (dataObject == null) {
                        return null;
                    }
                    FileObject fileObject = dataObject.getPrimaryFile();
                    prj = FileOwnerQuery.getOwner(fileObject);
                }
            }

            return prj;
        }
    }
}
