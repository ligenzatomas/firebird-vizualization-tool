/**
 * Copyright 2008  Eugene Creswick
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ciscavate.cjwizard;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ciscavate.cjwizard.pagetemplates.DefaultPageTemplate;
import org.ciscavate.cjwizard.pagetemplates.PageTemplate;
import org.ciscavate.utilities.ExceptionUtilities;

/**
 * This is the primary "Wizard" class.  It must be instantiated with a
 * PageFactory and then treated as a JPanel.
 * 
 * @author rcreswick
 *
 */
public class WizardContainer extends JPanel implements WizardController {

   /**
    * Commons logging log instance
    */
   private static Log log = LogFactory.getLog(WizardContainer.class);
   
   /**
    * Storage for all the collected information.
    */
   private WizardSettings _settings;

   /**
    * The path from the start of the dialog to the current location.
    */
   private final List<WizardPage> _path = new LinkedList<WizardPage>();
   
   /**
    * The path of already-visited pages starting from the current page.
    */
   private final List<WizardPage> _visitedPath = new LinkedList<WizardPage>();
   
   /**
    * List of listeners to update on wizard events.
    */
   private final List<WizardListener> _listeners =
      new LinkedList<WizardListener>();

   /**
    * The template to surround the wizard pages of this dialog.
    */
   private PageTemplate _template = null;

   /**
    * The factory that generates pages for this wizard.
    */
   private final PageFactory _factory;
   
   /**
    * The panel containing any dynamically-added buttons.
    */
   private JPanel _extraButtonPanel;
   
   private final AbstractAction _prevAction = new AbstractAction("< Prev"){
      {
         setEnabled(false);
      }
      @Override
      public void actionPerformed(ActionEvent e) {
         prev();
      }
   };
   
   private final AbstractAction _nextAction = new AbstractAction("Next >"){
      @Override
      public void actionPerformed(ActionEvent e) {
         next();
      }
   };

   private final AbstractAction _finishAction = new AbstractAction("Finish"){
      {
         setEnabled(false);
      }
      @Override
      public void actionPerformed(ActionEvent e) {
         finish();
      }
   };
   
   private final AbstractAction _cancelAction = new AbstractAction("Cancel"){
      @Override
      public void actionPerformed(ActionEvent e) {
         cancel();
      }
   };
   
   /**
    * Constructor, uses default PageTemplate and {@link StackWizardSettings}.
    */
   public WizardContainer(PageFactory factory){
      this(factory, new DefaultPageTemplate(), new StackWizardSettings());
   }
   
   /**
    * Constructor.
    */
   public WizardContainer(PageFactory factory, PageTemplate template,
                          WizardSettings settings){
      _factory = factory;
      _template = template;
      _settings = settings;
     
      initComponents();
      _template.registerController(this);
      
      // get the first page:
      next();
   }

   /**
    * 
    */
   private void initComponents() {
      final JButton prevBtn = new JButton(_prevAction);
      final JButton nextBtn = new JButton(_nextAction);
      final JButton finishBtn = new JButton(_finishAction);
      final JButton cancelBtn = new JButton(_cancelAction);
      
      _extraButtonPanel = new JPanel();
      _extraButtonPanel.setLayout(
            new BoxLayout(_extraButtonPanel, BoxLayout.LINE_AXIS));
      
      final JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
      buttonPanel.add(_extraButtonPanel);
      buttonPanel.add(Box.createHorizontalGlue());
      buttonPanel.add(prevBtn);
      buttonPanel.add(Box.createHorizontalStrut(5));
      buttonPanel.add(nextBtn);
      buttonPanel.add(Box.createHorizontalStrut(10));
      buttonPanel.add(finishBtn);
      buttonPanel.add(Box.createHorizontalStrut(10));
      buttonPanel.add(cancelBtn);
      
      buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
      this.setLayout(new BorderLayout());
      
      this.add(_template, BorderLayout.CENTER);
      this.add(buttonPanel, BorderLayout.SOUTH);
   }
   
   /**
    * Add additional buttons to the wizard controls. Any previously-added
    * buttons are cleared on each call to this method.
    * 
    * @param buttons
    *           The buttons to add to the wizard controls.
    */
   public void setButtons(JButton... buttons)
   {
      _extraButtonPanel.removeAll();
      for (JButton button : buttons)
      {
         _extraButtonPanel.add(button);
         _extraButtonPanel.add(Box.createHorizontalStrut(10));
      }
   }
   
   /**
    * The PageFactory is not queried for pages when moving *backwards*.
    */
   public void prev() {
      log.debug("prev. page");
      
      // store visited pages
      WizardPage removing = _path.remove(_path.size() - 1);
      _visitedPath.add(0, removing);
      // update roll-back the settings:
      removing.updateSettings(getSettings());
      getSettings().rollBack();
      
      assert 1 <= _path.size() : "Invalid path size! "+_path.size();
      setPrevEnabled(_path.size() > 1);
      
      WizardPage curPage =  _path.get(_path.size() - 1);
      
      setNextEnabled(true);
      // tell the page that it is about to be rendered:
      curPage.rendering(getPath(), getSettings());
      _template.setPage(curPage);

      firePageChanged(curPage, getPath());
   }
   
   /**
    * 
    */
   public void next() {
      log.debug("next page");

      WizardPage lastPage = currentPage();
      if (null != lastPage) {
         // get the settings from the page that is going away:
         getSettings().newPage(lastPage.getId());
         lastPage.updateSettings(getSettings());
      }
      
      ///TODO [dpd] this won't work with multiple paths
      WizardPage nextPage = null;
      if (_visitedPath.isEmpty()) {
         nextPage = _factory.createPage(getPath(), getSettings());
      } else {
         nextPage = _visitedPath.remove(0);
      }
         
      nextPage.registerController(this);
     
      _path.add(nextPage);
      setPrevEnabled(_path.size() > 1);
      
      // tell the page that it is about to be rendered:
      nextPage.rendering(getPath(), getSettings());
      _template.setPage(nextPage);

      firePageChanged(nextPage, getPath());
   }

   public void visitPage(WizardPage page){
      int idx = _path.indexOf(page);
      
      WizardPage lastPage = currentPage();
      if (null != lastPage) {
         // update the settings before leaving
         lastPage.updateSettings(getSettings());
      }
      
      if (-1 == idx){
         // new page
         if (null != lastPage) {
            // get the settings from the page that is going away:
            getSettings().newPage(lastPage.getId());
         }
         
         // add back all visited pages
         while (!_visitedPath.isEmpty()) {
            WizardPage visited = _visitedPath.remove(0);
            getPath().add(visited);
            if (visited == page)
               break;
         }
         // this shouldn't happen
         if (currentPage() != page) {
            getPath().add(page);
         }
      } else {
         // page is in the path at idx.
         
         // first, roll back the settings and trim the path:
         for (int i=_path.size()-1; i > idx; i--){
            getSettings().rollBack();
            // save visited pages
            _visitedPath.add(0, _path.remove(i));
         }
      }
      
      setPrevEnabled(_path.size() > 1);

      setNextEnabled(true);
      page.rendering(_path, getSettings());
      _template.setPage(page);
      firePageChanged(page, _path);
   }
   
   /**
    * @param nextPage
    * @param path
    */
   private void firePageChanged(WizardPage curPage, List<WizardPage> path) {
      for (WizardListener l : _listeners) {
         l.onPageChanged(curPage, getPath());
      }
   }

   /**
    * 
    */
   public void finish() {
      log.debug("finish");
      
      for (WizardListener l : _listeners) {
         l.onFinished(getPath(), getSettings());
      }
   }
   
   /**
    * 
    */
   public void cancel() {
      log.debug("cancel");
      
      for (WizardListener l : _listeners) {
         l.onCanceled(getPath(), getSettings());
      }
   }
   
   /* (non-Javadoc)
    * @see org.ciscavate.cjwizard.WizardController#addWizardListener(com.stottlerhenke.presentwell.wizard.WizardListener)
    */
   public void addWizardListener(WizardListener listener){
      ExceptionUtilities.checkNull(listener, "listener");
      if (!_listeners.contains(listener)){
         _listeners.add(listener);
         WizardPage curPage = _path.get(_path.size()-1);
         listener.onPageChanged(curPage, getPath());
      }
   }
   
   /* (non-Javadoc)
    * @see org.ciscavate.cjwizard.WizardController#removeWizardListener(com.stottlerhenke.presentwell.wizard.WizardListener)
    */
   public void removeWizardListener(WizardListener listener){
      ExceptionUtilities.checkNull(listener, "listener");
      _listeners.remove(listener);
   }
   
   /* (non-Javadoc)
    * @see org.ciscavate.cjwizard.WizardController#getSettings()
    */
   public WizardSettings getSettings(){
      return _settings;
   }
   
   /**
    * Set/load the specified settings map nad re-render the current page.
    * 
    * @param settings
    *           The settings to load.
    */
   public void setSettings(WizardSettings settings)
   {
      _settings = settings;
      currentPage().rendering(_path, _settings);
   }
   
   /* (non-Javadoc)
    * @see org.ciscavate.cjwizard.WizardController#getPath()
    */
   public List<WizardPage> getPath() {
      return _path;
   }
   
   /**
    * @return The last (current) page of the current {@link #_path} or null if
    *         the path is empty.
    */
   public WizardPage currentPage() {
      int lastIdx = _path.size() - 1;
      return (lastIdx < 0) ? null : _path.get(lastIdx);
   }

   /* (non-Javadoc)
    * @see org.ciscavate.cjwizard.WizardController#setNextEnabled(boolean)
    */
   public void setNextEnabled(boolean enabled) {
      _nextAction.setEnabled(enabled);
   }
   
   public void setNextText(String text) {
      _nextAction.putValue(Action.NAME, text);
   }
   
   /* (non-Javadoc)
    * @see org.ciscavate.cjwizard.WizardController#setPrevEnabled(boolean)
    */
   public void setPrevEnabled(boolean enabled) {
      _prevAction.setEnabled(enabled);
   }

   /* (non-Javadoc)
    * @see org.ciscavate.cjwizard.WizardController#setFinishEnabled(boolean)
    */
   public void setFinishEnabled(boolean enabled) {
      _finishAction.setEnabled(enabled);
   }

}
