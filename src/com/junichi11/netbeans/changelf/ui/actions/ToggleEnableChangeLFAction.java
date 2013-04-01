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
import com.junichi11.netbeans.changelf.ui.options.ChangeLFOptions;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
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
@Messages("CTL_ToggleEnableChangeLFAction=Toggle enable change LF")
public final class ToggleEnableChangeLFAction extends BooleanStateAction {

    private static final long serialVersionUID = -538737826355249808L;
    private static final Logger LOGGER = Logger.getLogger(ToggleEnableChangeLFAction.class.getName());
    private static final String LF_ICON_16 = "com/junichi11/netbeans/changelf/resources/lf_16.png"; // NOI18N
    private static final String CRLF_ICON_16 = "com/junichi11/netbeans/changelf/resources/crlf_16.png"; // NOI18N
    private static final String CR_ICON_16 = "com/junichi11/netbeans/changelf/resources/cr_16.png"; // NOI18N

    @Override
    public void actionPerformed(ActionEvent e) {
        ChangeLFOptions options = ChangeLFOptions.getInstance();
        boolean enable = options.isEnable();
        options.setEnable(!enable);
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
        ChangeLFOptions options = ChangeLFOptions.getInstance();
        String lfKind = options.getLfKind();
        return getIconResource(lfKind);
    }

    @Override
    protected void initialize() {
        super.initialize();
        ChangeLFOptions options = ChangeLFOptions.getInstance();
        setBooleanState(options.isEnable());
    }

    /**
     * Get icon resource for line endings.
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
}
