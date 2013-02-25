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
package com.junichi11.netbeans.changelf.api;

import javax.swing.text.Document;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;

/**
 *
 * @author junichi11
 */
public interface ChangeLF {

    public enum TYPE {

        LF("\n"), // NOI18N
        CRLF("\r\n"), // NOI18N
        CR("\r"); // NOI18N
        private final String lineSeparator;

        private TYPE(String lineSeparator) {
            this.lineSeparator = lineSeparator;
        }

        public String getLineSeparator() {
            return lineSeparator;
        }
    }

    /**
     * Change line feed code with User settings. User settings: Tools > Options
     * > Editor > ChangeLF
     *
     * <p>Usage:<br>
     * <code>
     * changeLf = Lookup.getDefault().lookup(ChangeLF.class);<br>
     * changeLf.change(doc);
     * </code></p>
     *
     * @since 0.2
     * @param doc Document
     */
    void change(Document doc);

    /**
     * Change the line feed code to force.
     *
     * @since 0.2
     * @param doc Document
     * @param type LF, CR, CRLF
     * @param useDialog whether display the dialog window before change lf code.
     */
    void change(Document doc, TYPE type, boolean useDialog);

    /**
     * Get current line feed code. Return user setting. If line endings is set
     * on specified project and it is enabled, return it. If project is null or
     * line endings is not enabled, find global option.
     *
     * @since 0.3
     * @param project
     * @return current type if enable is checked, otherwise null.
     */
    TYPE getCurrentLineFeedCode(Project project);

    /**
     * Get CompositeCategoryProvider.
     *
     * <p><code>@ProjectCustomizer.CompositeCategoryProvider.Registration(projectType =
     * "your_project_path", position = 5000)<br>
     * public static ProjectCustomizer.CompositeCategoryProvider
     * createChangeLF() { ChangeLF changeLf =
     * Lookup.getDefault().lookup(ChangeLF.class); return
     * changeLf.getCompositCategoryProvider(); }
     * </code></p>
     *
     * @since 0.3
     * @see ProjectCustomizer.CompositeCategoryProvider
     * @return ProjectCustomizer.CompositeCategoryProvider instance
     */
    ProjectCustomizer.CompositeCategoryProvider getCompositCategoryProvider();
}
