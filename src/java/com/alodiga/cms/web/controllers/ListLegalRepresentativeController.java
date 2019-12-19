package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.custom.components.ListcellEditButton;
import com.alodiga.cms.web.custom.components.ListcellViewButton;
import com.alodiga.cms.web.generic.controllers.GenericAbstractListController;
import com.alodiga.cms.web.utils.Utils;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.LegalRepresentatives;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

public class ListLegalRepresentativeController extends GenericAbstractListController<LegalRepresentatives> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Tab tabAddress;
    private Textbox txtName;
    private UtilsEJB utilsEJB = null;
    private List<LegalRepresentatives> legalRepresentatives = null;

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
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            getData();
            loadDataList(legalRepresentatives);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    /*public List<LegalRepresentatives> getFilteredList(String filter) {
     List<LegalRepresentatives> legalRepresentativesaux = new ArrayList<LegalRepresentatives>();
     LegalRepresentatives legalRepresentatives;
     try {
     if (filter != null && !filter.equals("")) {
     legalRepresentatives = utilsEJB.searchRequest(filter);
     legalRepresentativesaux.add(legalRepresentatives);
     } else {
     return legalRepresentatives;
     }
     } catch (RegisterNotFoundException ex) {
     Logger.getLogger(ListRequestController.class.getName()).log(Level.SEVERE, null, ex);
     } catch (Exception ex) {
     showError(ex);
     }
     return legalRepresentativesaux;
     }*/
    
    
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

    public void onClick$btnDelete() {
    }

    public void loadDataList(List<LegalRepresentatives> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                //btnDownload.setVisible(true);
                for (LegalRepresentatives legalRepresentatives : list) {
                    item = new Listitem();
                    item.setValue(legalRepresentatives);
                    StringBuilder builder = new StringBuilder(legalRepresentatives.getFirstNames());
                    builder.append(" ");
                    builder.append(legalRepresentatives.getLastNames());
                    String pattern = "yyyy-MM-dd";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    item.appendChild(new Listcell(builder.toString()));
                    item.appendChild(new Listcell(legalRepresentatives.getDocumentsPersonTypeId().getDescription()));
                    item.appendChild(new Listcell(legalRepresentatives.getIdentificationNumber()));
                    item.appendChild(new Listcell(simpleDateFormat.format(legalRepresentatives.getDueDateDocumentIdentification())));
                    item.appendChild(new Listcell(simpleDateFormat.format(legalRepresentatives.getDateBirth())));
                    item.appendChild(createButtonEditModal(legalRepresentatives));
                    item.appendChild(createButtonViewModal(legalRepresentatives));
                    //item.appendChild(permissionRead ? new ListcellViewButton(adminPage, legalRepresentatives) : new Listcell());
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
        legalRepresentatives = new ArrayList<LegalRepresentatives>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            legalRepresentatives = utilsEJB.getLegalRepresentativeses(request);
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

//    public void onClick$btnSearch() throws InterruptedException {
//        try {
//            loadList(getFilteredList(txtAlias.getText()));
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }
    @Override
    public List<LegalRepresentatives> getFilterList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /*public void loadDataList(List<LegalRepresentatives> list) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }*/

}
