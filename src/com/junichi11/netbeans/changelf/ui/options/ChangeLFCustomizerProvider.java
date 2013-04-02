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
package com.junichi11.netbeans.changelf.ui.options;

import javax.swing.JComponent;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class ChangeLFCustomizerProvider implements ProjectCustomizer.CompositeCategoryProvider {

    private static final String CHANGE_LF = "ChangeLF"; // NOI18N
    private String name;

    @ProjectCustomizer.CompositeCategoryProvider.Registrations({
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-ant-freeform", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-apisupport-project", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-j2ee-clientproject", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-j2ee-earproject", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-j2ee-ejbjarproject", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-java-j2seproject", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-maven", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-php-project", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-web-project", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-web.clientproject", position = 5000),})
    public static ChangeLFCustomizerProvider createChangeLF() {
        return new ChangeLFCustomizerProvider(CHANGE_LF);
    }

    private ChangeLFCustomizerProvider(String name) {
        this.name = name;
    }

    @NbBundle.Messages("LBL_Config_ChangeLF=Line Endings")
    @Override
    public Category createCategory(Lookup lkp) {
        return Category.create(CHANGE_LF, Bundle.LBL_Config_ChangeLF(), null);
    }

    @Override
    public JComponent createComponent(Category category, Lookup lookup) {
        Project project = lookup.lookup(Project.class);
        return new ChangeLFCustomizerPanel(category, project);
    }
}
