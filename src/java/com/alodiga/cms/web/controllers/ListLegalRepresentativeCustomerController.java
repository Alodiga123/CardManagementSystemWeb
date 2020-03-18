package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractListController;
import com.alodiga.cms.web.utils.Utils;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.LegalCustomer;
import com.cms.commons.models.LegalCustomerHasLegalRepresentatives;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

public class ListLegalRepresentativeCustomerController extends GenericAbstractListController<LegalCustomerHasLegalRepresentatives> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtName;
    private PersonEJB personEJB = null;
    private List<LegalCustomerHasLegalRepresentatives> legalRepresentatives = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
        startListener();
    }

    public void startListener() {
        EventQueue que = EventQueues.lookup("updateLegalRepresentative", EventQueues.APPLICATION, true);
        que.subscribe(new EventListener() {

            public void onEvent(Event evt) {
                getData();
                loadDataList(legalRepresentatives);
            }
        });
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            //Evaluar Permisos
            permissionEdit = true;
            permissionAdd = true;
            permissionRead = true;
            adminPage = "/adminLegalRepresentative.zul";
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            getData();
            loadDataList(legalRepresentatives);
        } catch (Exception ex) {
            showError(ex);
        }
    } 
    
    public void onClick$btnAdd() throws InterruptedException {
        try {
            Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_ADD);
            Map<String, Object> paramsPass = new HashMap<String, Object>();
            paramsPass.put("object", legalRepresentatives);
            final Window window = (Window) Executions.createComponents(adminPage, null, paramsPass);
            window.doModal();
        } catch (Exception ex) {
            this.showMessage("sp.error.general", true, ex);
        }
    }

    public void loadDataList(List<LegalCustomerHasLegalRepresentatives> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                for (LegalCustomerHasLegalRepresentatives legalRepresentatives : list) {
                    item = new Listitem();
                    item.setValue(legalRepresentatives);
                    StringBuilder builder = new StringBuilder(legalRepresentatives.getLegalRepresentativesId().getFirstNames());
                    builder.append(" ");
                    builder.append(legalRepresentatives.getLegalRepresentativesId().getLastNames());
                    String pattern = "yyyy-MM-dd";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    item.appendChild(new Listcell(builder.toString()));
                    item.appendChild(new Listcell(legalRepresentatives.getLegalRepresentativesId().getDocumentsPersonTypeId().getDescription()));
                    item.appendChild(new Listcell(legalRepresentatives.getLegalRepresentativesId().getIdentificationNumber()));
                    item.appendChild(new Listcell(simpleDateFormat.format(legalRepresentatives.getLegalRepresentativesId().getDueDateDocumentIdentification())));
                    item.appendChild(new Listcell(simpleDateFormat.format(legalRepresentatives.getLegalRepresentativesId().getDateBirth())));
                    
                    item.appendChild(createButtonEditModal(legalRepresentatives.getLegalRepresentativesId()));
                    item.appendChild(createButtonViewModal(legalRepresentatives.getLegalRepresentativesId()));
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
    
    
    public Listcell createButtonEditModal(final Object obg) {
       Listcell listcellEditModal = new Listcell();
        try {    
            Button button = new Button();
            button.setImage("/images/icon-edit.png");
            button.setClass("open orange");
            button.addEventListener("onClick", new EventListener() {
                @Override
                public void onEvent(Event arg0) throws Exception {
                  Sessions.getCurrent().setAttribute("object", obg);  
                  Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_EDIT);
                  Map<String, Object> paramsPass = new HashMap<String, Object>();
                  paramsPass.put("object", obg);
                  final Window window = (Window) Executions.createComponents(adminPage, null, paramsPass);
                  window.doModal(); 
                }

            });
            button.setParent(listcellEditModal);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listcellEditModal;
    }
    
    
    public Listcell createButtonViewModal(final Object obg) {
       Listcell listcellViewModal = new Listcell();
        try {    
            Button button = new Button();
            button.setImage("/images/icon-invoice.png");
            button.setClass("open orange");
            button.addEventListener("onClick", new EventListener() {
                @Override
                public void onEvent(Event arg0) throws Exception {
                  Sessions.getCurrent().setAttribute("object", obg);  
                  Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_VIEW);
                  Map<String, Object> paramsPass = new HashMap<String, Object>();
                  paramsPass.put("object", obg);
                  final Window window = (Window) Executions.createComponents(adminPage, null, paramsPass);
                  window.doModal(); 
                }

            });
            button.setParent(listcellViewModal);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listcellViewModal;
    }
    
   
    public void getData() {
        legalRepresentatives = new ArrayList<LegalCustomerHasLegalRepresentatives>();
        LegalCustomer legalCustomer = null;
        try {
            //Solicitante de Tarjeta
            AdminLegalPersonCustomerController adminLegalPerson = new AdminLegalPersonCustomerController();
            if (adminLegalPerson.getLegalCustomer() != null) {
                legalCustomer = adminLegalPerson.getLegalCustomer();
            }
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.LEGAL_CUSTOMER_KEY, legalCustomer.getId());
            request1.setParams(params);
            legalRepresentatives = personEJB.getLegalRepresentativesesByCustomer(request1);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
            showEmptyList();
        } catch (GeneralException ex) {
            showError(ex);
        }
    }

    private void showEmptyList() {
        Listitem item = new Listitem();
        item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.appendChild(new Listcell());
        item.setParent(lbxRecords);
    }

    public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("cms.common.cardRequest.list"));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {
        txtName.setText("");
    }

    @Override
    public List<LegalCustomerHasLegalRepresentatives> getFilterList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}