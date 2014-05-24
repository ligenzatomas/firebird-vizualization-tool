/**
 * Copyright 2014 Tomáš Ligenza
 *
 * This file is part of Firebird Visualization Tool.
 *
 * Firebird Visualization Tool is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * TinyUML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Firebird Visualization Tool; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.tinyuml.ui.wizard;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import org.ciscavate.cjwizard.WizardPage;
import org.ciscavate.cjwizard.pagetemplates.PageTemplate;

/**
 *
 * @author Tomáš Ligenza
 */
public class WizardPageTemplate extends PageTemplate {
	
	private static final long serialVersionUID = 3131173121732512126L;
	
	private final JLabel _title = new JLabel();
	
	private final JPanel leftPanel = new JPanel();
	private final JPanel rightPanel = new JPanel();
	
	private final JPanel contentPage = new JPanel();
	
	private final ArrayList<JLabel> steps = new ArrayList<JLabel>();
   
	public WizardPageTemplate(String[] labels) {
		
		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		Border innerBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black);

		_title.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));

		this.setLayout(new BorderLayout());

		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(_title, BorderLayout.NORTH);
		rightPanel.add(contentPage, BorderLayout.CENTER);
		
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(leftPanel);
		leftPanel.setLayout(layout);
		
		GroupLayout.ParallelGroup pGroup = layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING);
		GroupLayout.SequentialGroup sGroup = layout.createSequentialGroup().addContainerGap();
		
		for(String l : labels) {
			
			JLabel jl = new JLabel(l);
			steps.add(jl);
			pGroup.addComponent(jl);
			sGroup.addComponent(jl).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED);
		}

		layout.setHorizontalGroup(
			layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(pGroup)
				.addContainerGap(100, Short.MAX_VALUE))
		);
		
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sGroup
                .addContainerGap(165, Short.MAX_VALUE))
        );
		
		leftPanel.setMinimumSize(new Dimension(50, 800));
		rightPanel.setMinimumSize(new Dimension(800, 800));
		contentPage.setMinimumSize(new Dimension(800, 750));
		
		contentPage.setLayout(new CardLayout());
		
		this.add(leftPanel, BorderLayout.WEST);
		this.add(rightPanel, BorderLayout.CENTER);
   }
   
	@Override
	public void setPage(final WizardPage page) {

		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {

				contentPage.validate();

				contentPage.add(page, page.getId());
				((CardLayout) contentPage.getLayout()).show(contentPage, page.getId());

				_title.setText(page.getDescription());

				for(JLabel j : steps) {

					if(j.getText().equals(page.getTitle())) {
						j.setFont(new Font(j.getFont().getFontName(), Font.BOLD, j.getFont().getSize()));
					} else {
						j.setFont(j.getFont().deriveFont(Font.PLAIN));
					}
				}
			}
		});
	}
}
