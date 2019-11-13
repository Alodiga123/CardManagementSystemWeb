package com.alodiga.cms.web.controllers;


import com.cms.commons.models.User;
import com.cms.commons.util.Constants;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;


public class AdminMainMenuController extends GenericForwardComposer {

    private static final long serialVersionUID = -9145887024839938515L;
    Listcell ltcFullName;
    Listcell ltcProfile;
    Listcell ltcLogin;
    
    
    
    private static String OPTION = "option";
    private static String OPTION_NONE = "none";
    private static String OPTION_CUSTOMERS_LIST = "ltcCustomerList";
    
    private Listbox lbxPermissions;

    private Long languageId;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }

    public void initialize() {
        try {
            loadAccountData();
            
            checkOption();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void checkOption() {
//        String option = getOptionInSession();
//        if (option.equals(OPTION_NONE)) {
//        } else if (option.equals(OPTION_CUSTOMERS_LIST)) {
//            //ltcCustomerList.setImage("/images/icon-target.png");
//        }
    }

    private void loadAccountData() {   
       User user = (User) session.getAttribute(Constants.USER_OBJ_SESSION);
       ltcFullName.setLabel(user.getFirstName() + " " + user.getLastName());
       //TODO:
       ltcProfile.setLabel("Administrador");
       ltcLogin.setLabel(user.getLogin());
       ///  Opcion CardRequestType   
            Listitem item = new Listitem();
            Listcell listCell = new Listcell();
            listCell.setLabel("cms.menu.cardRequestType.list");
            listCell.addEventListener("onClick", new RedirectListener("listCardRequestType.zul"));

            listCell.setParent(item);
            item.setParent(lbxPermissions);
            
        /// Opcion Country
            Listitem item2 = new Listitem();
            Listcell listCell2 = new Listcell();
            listCell2.setLabel( Labels.getLabel("sp.common.country"));
            listCell2.addEventListener("onClick", new RedirectListener("listCountry.zul"));
            listCell2.setParent(item2);
            item2.setParent(lbxPermissions);
            
        /// Opcion Currency
            Listitem item3 = new Listitem();
            Listcell listCell3 = new Listcell();
            listCell3.setLabel(Labels.getLabel("sp.common.currency"));
            listCell3.addEventListener("onClick", new RedirectListener("listCurrency.zul"));
            listCell3.setParent(item3);
            item3.setParent(lbxPermissions);  

            /// Opcion statusCard
            Listitem item4 = new Listitem();
            Listcell listCell4 = new Listcell();
            listCell4.setLabel(Labels.getLabel("sp.common.statusCard"));
            listCell4.addEventListener("onClick", new RedirectListener("listCardStatus.zul"));
            listCell4.setParent(item4);
            item4.setParent(lbxPermissions);  
            
            
            /// Opction status
            Listitem item5 = new Listitem();
            Listcell listCell5 = new Listcell();
            listCell5.setLabel(Labels.getLabel("sp.common.status"));
            listCell5.addEventListener("onClick", new RedirectListener("listStatusRequest.zul"));
            listCell5.setParent(item5);
            item5.setParent(lbxPermissions);  
            
            /// Opction collectionRequest
            Listitem item6 = new Listitem();
            Listcell listCell6 = new Listcell();
            listCell6.setLabel(Labels.getLabel("cms.common.collectionRequest"));
            listCell6.addEventListener("onClick", new RedirectListener("listCollectionRequest.zul"));
            listCell6.setParent(item6);
            item6.setParent(lbxPermissions); 
             
            // Option Classification_Person
            Listitem item7 = new Listitem();
            Listcell listCell7 = new Listcell();
            listCell7.setLabel( Labels.getLabel("sp.commom.Classification.Person"));
            listCell7.addEventListener("onClick", new RedirectListener("listClassificationPerson.zul"));
            listCell7.setParent(item7);
            item7.setParent(lbxPermissions); 
            
             // Option State
            Listitem item8 = new Listitem();
            Listcell listCell8 = new Listcell();
            listCell8.setLabel( Labels.getLabel("sp.common.state"));
            listCell8.addEventListener("onClick", new RedirectListener("listState.zul"));
            listCell8.setParent(item8);
            item8.setParent(lbxPermissions); 
            
         }
    }

      class RedirectListener implements EventListener {

        private String view = null;
        private Long permissionId = null;
       
        public RedirectListener() {
        }

        public RedirectListener(String view, Long permissionId) {
            this.view = view;
            this.permissionId = permissionId;
        }
        
        
        public RedirectListener(String view) {
            this.view = view;
        }

        @Override
        public void onEvent(Event event) throws UiException, InterruptedException {
            
            Executions.sendRedirect(view);
       
        }
    }

