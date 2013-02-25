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
package com.junichi11.netbeans.changelf.preferences;

import com.junichi11.netbeans.changelf.ChangeLFImpl;
import com.junichi11.netbeans.changelf.ChangeLFUtils;
import java.util.prefs.Preferences;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;

/**
 *
 * @author junichi11
 */
public class ChangeLFPreferences {

    private static final String LF_KIND = "lf-kind"; // NOI18N
    private static final String SHOW_DIALOG = "show-dialog"; // NOI18N
    private static final String USE_GLOBAL = "use-global"; // NOI18N
    private static final String USE_PROJECT = "use-project"; // NOI18N
    private static final String ENABLE = "enable"; // NOI18N

    public static boolean isEnable(Project project) {
        return getPreferences(project).getBoolean(ENABLE, false);
    }

    public static boolean useGlobal(Project project) {
        return getPreferences(project).getBoolean(USE_GLOBAL, true);
    }

    public static boolean useProject(Project project) {
        return getPreferences(project).getBoolean(USE_PROJECT, false);
    }

    public static String getLfKind(Project project) {
        String name = ChangeLFUtils.toLineFeedCodeName(System.getProperty("line.separator")); // NOI18N
        if (name.isEmpty()) {
            name = ChangeLFImpl.LF;
        }
        return getPreferences(project).get(LF_KIND, name);
    }

    public static boolean showDialog(Project project) {
        return getPreferences(project).getBoolean(SHOW_DIALOG, false);
    }

    public static void setGlobal(Project project, boolean use) {
        getPreferences(project).putBoolean(USE_GLOBAL, use);
    }

    public static void setProject(Project project, boolean useProject) {
        getPreferences(project).putBoolean(USE_PROJECT, useProject);
    }

    public static void setEnable(Project project, boolean enable) {
        getPreferences(project).putBoolean(ENABLE, enable);
    }

    public static void setShowDialog(Project project, boolean useShowDialog) {
        getPreferences(project).putBoolean(SHOW_DIALOG, useShowDialog);
    }

    public static void setLfKind(Project project, String lfKind) {
        getPreferences(project).put(LF_KIND, lfKind);
    }

    private static Preferences getPreferences(Project project) {
        return ProjectUtils.getPreferences(project, ChangeLFPreferences.class, true);
    }
}
