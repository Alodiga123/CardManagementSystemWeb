package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractListController;
import com.alodiga.cms.web.utils.Utils;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.ApprovalCardRate;
import com.cms.commons.models.Product;
import com.cms.commons.models.ProductType;
import com.cms.commons.models.Program;
import com.cms.commons.models.User;
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
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ListApprovalRateByCardController extends GenericAbstractListController<ApprovalCardRate> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtName;
    private ProductEJB productEJB = null;
    private List<ApprovalCardRate> approvalCardRateList = null;
    private User currentUser;
    private Button btnSave;
    public Program program = null;
    public Product product = null;
    private ProductType productType = null;
    private ListRateByProgramController listRateByCard = null;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
        startListener();
    }

    public void startListener() {
        EventQueue que = EventQueues.lookup("updateApprovalCardRate", EventQueues.APPLICATION, true);
        que.subscribe(new EventListener() {
            public void onEvent(Event evt) { 
                getData();
                loadDataList(approvalCardRateList);
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
            program = (Program) session.getAttribute(WebConstants.PROGRAM);
            adminPage = "/adminApprovalCardRate.zul";
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);             
            getData();
            loadDataList(approvalCardRateList);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void getData() {
        approvalCardRateList = new ArrayList<ApprovalCardRate>();
        try {
            request.setFirst(0);
            request.setLimit(null);
            approvalCardRateList = productEJB.getApprovalCardRate(request);   
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        }
    }  

    public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("cms.crud.approvalProductRate.list"));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {
        txtName.setText("");
    }    
    
    public void loadDataList(List<ApprovalCardRate> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                for (ApprovalCardRate approvalCardRate : list) {
                    item = new Listitem();
                    item.setValue(approvalCardRate);
                    String pattern = "dd/MM/yyyy";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    StringBuilder builder = new StringBuilder(approvalCardRate.getUserId().getFirstNames());
                    builder.append(" ");
                    builder.append(approvalCardRate.getUserId().getLastNames());                    
                    item.appendChild(new Listcell(builder.toString()));
                    item.appendChild(new Listcell(approvalCardRate.getUserId().getComercialAgencyId().getName()));
                    item.appendChild(new Listcell(approvalCardRate.getCardId().getCardHolder()));
                    item.appendChild(new Listcell(simpleDateFormat.format(approvalCardRate.getApprovalDate())));               
                    item.appendChild(createButtonEditModal(approvalCardRate));
                    item.appendChild(createButtonViewModal(approvalCardRate));
                    item.setParent(lbxRecords);
                }
            } else {
//                btnDownload.setVisible(false);
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
    
    public void onClick$btnAdd() throws InterruptedException {
        try {
            Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_ADD);
            Map<String, Object> paramsPass = new HashMap<String, Object>();
            paramsPass.put("object", approvalCardRateList);
            final Window window = (Window) Executions.createComponents(adminPage, null, paramsPass);
            window.doModal();
        } catch (Exception ex) {
            this.showMessage("sp.error.general", true, ex);
        }
    }
    
    public Listcell createButtonEditModal(final Object obg) {
       Listcell listcellEditModal = new Listcell();
        try {    
            Button button = new Button();
            button.setImage("/images/icon-edit.png");
            button.setTooltiptext(Labels.getLabel("sp.common.actions.edit"));
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
            button.setTooltiptext(Labels.getLabel("sp.common.actions.view"));
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
    public List<ApprovalCardRate> getFilterList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}