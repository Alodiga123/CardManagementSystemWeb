package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.CardEJB;
import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.custom.components.ListcellEditButton;
import com.alodiga.cms.web.custom.components.ListcellViewButton;
import com.alodiga.cms.web.generic.controllers.GenericAbstractListController;
import com.alodiga.cms.web.utils.Utils;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.AccountProperties;
import com.cms.commons.models.User;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

public class ListAccountPropertiesController extends GenericAbstractListController<AccountProperties> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private List<AccountProperties> accountPropertiesList = null;
    private CardEJB cardEJB= null;
    private User currentUser;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            //Evaluar Permisos
            permissionEdit = true;
            permissionAdd = true;
            permissionRead = true;
            adminPage = "TabAccountManager.zul";
            currentUser = (User) session.getAttribute(Constants.USER_OBJ_SESSION);
            cardEJB = (CardEJB) EJBServiceLocator.getInstance().get(EjbConstants.CARD_EJB);
            getData();
            loadDataList(accountPropertiesList);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_ADD);
        Executions.getCurrent().sendRedirect(adminPage);
    }
    
    public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("sp.crud.enterprise.list"));
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public void getData() {
    accountPropertiesList = new ArrayList<AccountProperties>();     
        try {
            request.setFirst(0);
            request.setLimit(null);
            accountPropertiesList = cardEJB.getAccountProperties(request);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
        } catch (GeneralException ex) {
            showError(ex);
        }
    }
    
    public void startListener() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void loadDataList(List<AccountProperties> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {

                for (AccountProperties accountProperties : list) {
                    item = new Listitem();
                    item.setValue(accountProperties);
                    item.appendChild(new Listcell(accountProperties.getCountryId().getName().toString()));
                    item.appendChild(new Listcell(accountProperties.getIdentifier().toString()));
                    item.appendChild(new Listcell(accountProperties.getLenghtAccount().toString()));
                    item.appendChild(new Listcell(accountProperties.getAccountTypeId().getDescription()));
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, accountProperties) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, accountProperties) : new Listcell());
                    item.setParent(lbxRecords);
                }
            } else {
                btnDownload.setVisible(false);
                item = new Listitem();
                item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.setParent(lbxRecords);
            }

        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    @Override
    
    public List<AccountProperties> getFilterList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
   

}