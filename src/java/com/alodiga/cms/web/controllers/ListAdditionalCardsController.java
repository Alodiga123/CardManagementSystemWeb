package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.custom.components.ListcellEditButton;
import com.alodiga.cms.web.custom.components.ListcellViewButton;
import com.alodiga.cms.web.generic.controllers.GenericAbstractListController;
import com.alodiga.cms.web.utils.Utils;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.CardRequestNaturalPerson;
import com.cms.commons.models.User;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.ArrayList;
import java.util.List;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;

public class ListAdditionalCardsController extends GenericAbstractListController<CardRequestNaturalPerson> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtName;
    private UtilsEJB utilsEJB = null;
    private PersonEJB personEJB = null;
    private Tab tabAddress;
    private List<CardRequestNaturalPerson> cardRequestNaturalPerson = null;
    private User currentUser;
    private Button btnSave;

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
            adminPage = "adminAdditionalCards.zul";
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            getData();
            loadDataList(cardRequestNaturalPerson);
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public void startListener() {
    }

    public void getData() {
        cardRequestNaturalPerson = new ArrayList<CardRequestNaturalPerson>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            cardRequestNaturalPerson = personEJB.getCardRequestNaturalPersons(request);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
        } catch (GeneralException ex) {
            showError(ex);
        }
    }

    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute("eventType", WebConstants.EVENT_ADD);
        Sessions.getCurrent().removeAttribute("object");
        Executions.getCurrent().sendRedirect(adminPage);
    }

    public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("sp.crud.enterprise.list"));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {
        txtName.setText("");
    }


//    public List<RequestType> getFilterList(String filter) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    
    
    public void loadDataList(List<CardRequestNaturalPerson> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                //btnDownload.setVisible(true);
                for (CardRequestNaturalPerson cardRequestNaturalPerson : list) {
                    item = new Listitem();
                    item.setValue(cardRequestNaturalPerson);
                    StringBuilder builder = new StringBuilder(cardRequestNaturalPerson.getFirstNames());
                    builder.append(" ");
                    builder.append(cardRequestNaturalPerson.getLastNames());                    
                    item.appendChild(new Listcell(builder.toString()));
                    item.appendChild(new Listcell(cardRequestNaturalPerson.getDocumentsPersonTypeId().getDescription()));
                    item.appendChild(new Listcell(cardRequestNaturalPerson.getIdentificationNumber()));
                    item.appendChild(new Listcell(cardRequestNaturalPerson.getPositionEnterprise()));
                    item.appendChild(new Listcell(cardRequestNaturalPerson.getProposedLimit().toString()));
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, cardRequestNaturalPerson) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, cardRequestNaturalPerson) : new Listcell());
                    item.setParent(lbxRecords);
                }
            } else {
                btnDownload.setVisible(false);
                item = new Listitem();
                item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
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
    public List<CardRequestNaturalPerson> getFilterList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
