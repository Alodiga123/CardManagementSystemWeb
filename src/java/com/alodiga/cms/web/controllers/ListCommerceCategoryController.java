package com.alodiga.cms.web.controllers;



import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.web.controllers.*;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractListController;
import com.alodiga.cms.web.utils.Utils;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Product;
import com.cms.commons.models.ProductHasCommerceCategory;
import com.cms.commons.models.ProgramHasNetwork;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.util.resource.Labels;
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

public class ListCommerceCategoryController extends GenericAbstractListController<ProductHasCommerceCategory> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtName;
    private ProductEJB productEJB = null;
    private List<ProductHasCommerceCategory> productHasCommerceCategory = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
        startListener();
    }

    public void startListener() {
        EventQueue que = EventQueues.lookup("updateCommerceCategory", EventQueues.APPLICATION, true);
        que.subscribe(new EventListener() {

            public void onEvent(Event evt) {
                getData();
                loadDataList(productHasCommerceCategory);
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
            adminPage = "/adminAddCommerceCategory.zul";
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            getData();
            loadDataList(productHasCommerceCategory);
        } catch (Exception ex) {
            showError(ex);
        }
    }


    public void onClick$btnAdd() throws InterruptedException {
        try {
            Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_ADD);
            Map<String, Object> paramsPass = new HashMap<String, Object>();
            paramsPass.put("object", productHasCommerceCategory);
            final Window window = (Window) Executions.createComponents(adminPage, null, paramsPass);
            window.doModal();
        } catch (Exception ex) {
            this.showMessage("sp.error.general", true, ex);
        }
    }

    public void onClick$btnDelete() {
    }

    public void loadDataList(List<ProductHasCommerceCategory> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                //btnDownload.setVisible(true);
                for (ProductHasCommerceCategory productHasCommerceCategory : list) {
                    item = new Listitem();
                    item.setValue(productHasCommerceCategory);
                    item.appendChild(new Listcell(productHasCommerceCategory.getCommerceCategoryId().getMccCode()));
                    item.appendChild(new Listcell(productHasCommerceCategory.getCommerceCategoryId().getEconomicActivity()));
                    item.appendChild(new Listcell(productHasCommerceCategory.getCommerceCategoryId().getsegmentCommerceId().getName()));
                    item.appendChild(createButtonEditModal(productHasCommerceCategory));
                    item.appendChild(createButtonViewModal(productHasCommerceCategory));
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
        productHasCommerceCategory = new ArrayList<ProductHasCommerceCategory>();
        Product product = null;
        try {
             //Producto principal
            AdminProductController adminProduct = new AdminProductController();
            if (adminProduct.getProductParent().getId() != null) {
                product = adminProduct.getProductParent();
            }
            EJBRequest request = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PRODUCT_KEY, product.getId());
            request.setParams(params);
            productHasCommerceCategory = productEJB.getCommerceCategoryByProduct(request);
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
    public List<ProductHasCommerceCategory> getFilterList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
