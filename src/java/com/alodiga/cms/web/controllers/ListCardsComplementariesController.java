package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.custom.components.ListcellEditButton;
import com.alodiga.cms.web.custom.components.ListcellViewButton;
import com.alodiga.cms.web.generic.controllers.GenericAbstractListController;
import com.alodiga.cms.web.utils.Utils;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.ApplicantNaturalPerson;
import com.cms.commons.models.User;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ListCardsComplementariesController extends GenericAbstractListController<ApplicantNaturalPerson> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtName;
    private PersonEJB personEJB = null;
    private Tab tabAddress;
    private List<ApplicantNaturalPerson> cardComplementaryList = null;
    private User currentUser;
    private Button btnSave;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
        startListener();
    }
    
    
    public void startListener() {
        EventQueue que = EventQueues.lookup("updateCardComplementaries", EventQueues.APPLICATION, true);
        que.subscribe(new EventListener() {
            public void onEvent(Event evt) {
                getData();
                loadDataList(cardComplementaryList);
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
            //adminPage = "adminCardComplementaries.zul";
            adminPage = "TabCardsComplementaries.zul";
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            getData();
            loadDataList(cardComplementaryList);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void getData() {
        cardComplementaryList = new ArrayList<ApplicantNaturalPerson>();
        ApplicantNaturalPerson applicantNaturalPerson = null;
        try {
            //Solicitante de Tarjeta
            AdminNaturalPersonController adminNaturalPerson = new AdminNaturalPersonController();
            if (adminNaturalPerson.getApplicantNaturalPerson() != null) {
                applicantNaturalPerson = adminNaturalPerson.getApplicantNaturalPerson();
            }
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.APPLICANT_NATURAL_PERSON_KEY, applicantNaturalPerson.getId());
            request1.setParams(params);
            cardComplementaryList = personEJB.getCardComplementaryByApplicant(request1);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
        } catch (GeneralException ex) {
            showError(ex);
        }
    }

   /* public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute("eventType", WebConstants.EVENT_ADD);
        Sessions.getCurrent().removeAttribute("object");
        Executions.getCurrent().sendRedirect(adminPage);
        
    }*/
    


    public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("cms.crud.additionalCards.list"));
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
    
    
    public void loadDataList(List<ApplicantNaturalPerson> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                //btnDownload.setVisible(true);
                for (ApplicantNaturalPerson applicantNaturalPerson : list) {
                    item = new Listitem();
                    item.setValue(applicantNaturalPerson);
                    StringBuilder builder = new StringBuilder(applicantNaturalPerson.getFirstNames());
                    builder.append(" ");
                    builder.append(applicantNaturalPerson.getLastNames());                    
                    item.appendChild(new Listcell(builder.toString()));
                    item.appendChild(new Listcell(applicantNaturalPerson.getDocumentsPersonTypeId().getDescription()));
                    item.appendChild(new Listcell(applicantNaturalPerson.getIdentificationNumber()));
                    item.appendChild(new Listcell(applicantNaturalPerson.getKinShipApplicantId().getDescription()));
                    item.appendChild(createButtonEditModal(applicantNaturalPerson));
                    item.appendChild(createButtonViewModal(applicantNaturalPerson));
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
    
       public void onClick$btnAdd() throws InterruptedException {
        try {
            Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_ADD);
            Map<String, Object> paramsPass = new HashMap<String, Object>();
            paramsPass.put("object", cardComplementaryList);
            final Window window = (Window) Executions.createComponents(adminPage, null, paramsPass);
            window.doModal();
        } catch (Exception ex) {
            this.showMessage("sp.error.general", true, ex);
        }
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

    @Override
    public List<ApplicantNaturalPerson> getFilterList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
