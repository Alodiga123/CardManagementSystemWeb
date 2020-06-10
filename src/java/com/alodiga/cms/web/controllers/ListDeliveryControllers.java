package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.CardEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.custom.components.ListcellEditButton;
import com.alodiga.cms.web.custom.components.ListcellViewButton;
import com.alodiga.cms.web.generic.controllers.GenericAbstractListController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.DeliveryRequest;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.ArrayList;
import java.util.List;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

public class ListDeliveryControllers extends GenericAbstractListController<DeliveryRequest> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtName;
    private CardEJB cardEJB = null;
    private List<DeliveryRequest> deliveryRequests = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            adminPage = "TabDelivery.zul";
            cardEJB = (CardEJB) EJBServiceLocator.getInstance().get(EjbConstants.CARD_EJB);
            getData();
            loadDataList(deliveryRequests);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void getData() {
        deliveryRequests = new ArrayList<DeliveryRequest>();
        try {
            request.setFirst(0);
            request.setLimit(null);            
            deliveryRequests = cardEJB.getDeliveryRequest(request);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        }
    }

    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute("eventType", WebConstants.EVENT_ADD);
        Sessions.getCurrent().removeAttribute("object");
        Executions.getCurrent().sendRedirect(adminPage);
    }

    public void onClick$btnClear() throws InterruptedException {
        txtName.setText("");
    }

    public void startListener() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void loadDataList(List<DeliveryRequest> list) {
        Listcell tmpCell = new Listcell();
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                for (DeliveryRequest deliveryRequest : list) {

                    item = new Listitem();
                    item.setValue(deliveryRequest);
                    item.appendChild(new Listcell(deliveryRequest.getRequestNumber()));
                    item.appendChild(new Listcell(deliveryRequest.getShippingCompanyId().getEnterpriseName()));
                    item.appendChild(new Listcell(deliveryRequest.getProgramId().getName()));
                    item.appendChild(new Listcell(deliveryRequest.getStatusDeliveryRequestId().getDescription()));
                    item.appendChild(new ListcellEditButton(adminPage, deliveryRequest));
                    item.appendChild(new ListcellViewButton(adminPage, deliveryRequest, true));
                    item.setParent(lbxRecords);
                }
            } else {
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
    public List<DeliveryRequest> getFilterList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
