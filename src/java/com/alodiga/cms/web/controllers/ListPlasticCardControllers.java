package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.CardEJB;
import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.ejb.RequestEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.commons.exception.RegisterNotFoundException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractListController;
import com.alodiga.cms.web.utils.Utils;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Card;
import com.cms.commons.models.CardStatus;
import com.cms.commons.models.PlastiCustomizingRequestHasCard;
import com.cms.commons.models.PlasticCustomizingRequest;
import com.cms.commons.models.Product;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.cms.commons.util.QueryConstants;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ListPlasticCardControllers extends GenericAbstractListController<Card> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtName;
    private Label lblProgram;
    private Combobox cmbProgram;
    private Combobox cmbProduct;
    private UtilsEJB utilsEJB = null;
    private ProgramEJB programEJB = null;
    private ProductEJB productEJB = null;
    private RequestEJB requestEJB = null;
    private CardEJB cardEJB = null;
    private List<Card> plasticCard = null;
    private PlasticCustomizingRequest plastiCustomerParam;
    private Product product = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            adminPage = "adminPlasticCard.zul";
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            programEJB = (ProgramEJB) EJBServiceLocator.getInstance().get(EjbConstants.PROGRAM_EJB);
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            requestEJB = (RequestEJB) EJBServiceLocator.getInstance().get(EjbConstants.REQUEST_EJB);
            cardEJB = (CardEJB) EJBServiceLocator.getInstance().get(EjbConstants.CARD_EJB);
            loadField();

        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void startListener() {
        EventQueue que = EventQueues.lookup("updatePlasticCard", EventQueues.APPLICATION, true);
        que.subscribe(new EventListener() {

            public void onEvent(Event evt) {
                getData();
                loadDataList(plasticCard);
            }
        });
    }

    public void onClick$btnViewCard() throws InterruptedException {
        product = (Product) cmbProduct.getSelectedItem().getValue();
        getData();
        loadDataList(plasticCard);
    }

    private void loadField() {
        AdminPlasticRequestController adminPlasticRequest = new AdminPlasticRequestController();
        if (adminPlasticRequest.getPlasticCustomizingRequest().getId() != null) {
            plastiCustomerParam = adminPlasticRequest.getPlasticCustomizingRequest();
        }

        lblProgram.setValue(plastiCustomerParam.getProgramId().getName());
        loadCmbProduct(WebConstants.EVENT_ADD, plastiCustomerParam.getProgramId().getId());
    }

    public void getData() {
        plasticCard = new ArrayList<Card>();

        try {
            EJBRequest statusId = new EJBRequest();
            statusId = new EJBRequest();
            statusId.setParam(Constants.STATUS_CARDS_REQUESTED);
            CardStatus cardsRequested = utilsEJB.loadCardStatus(statusId);

            EJBRequest productId = new EJBRequest();
            Map params = new HashMap();
            params.put(QueryConstants.PARAM_PROGRAM_ID, productId);

            EJBRequest request2 = new EJBRequest();
            params = new HashMap();
            params.put(QueryConstants.PARAM_PROGRAM_ID, plastiCustomerParam.getProgramId().getId());
            params.put(Constants.PRODUCT_KEY, ((Product) cmbProduct.getSelectedItem().getValue()).getId());
            params.put(QueryConstants.PARAM_CARDS_STATUS_ID, cardsRequested.getId());
            request2.setParams(params);

            plasticCard = cardEJB.getCardByProgramByStatus(request2);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (RegisterNotFoundException ex) {
            Logger.getLogger(ListPlasticCardControllers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EmptyListException ex) {
            Logger.getLogger(ListPlasticCardControllers.class.getName()).log(Level.SEVERE, null, ex);
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

    public void loadDataList(List<Card> list) {
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                for (Card plasticCard : list) {

                    item = new Listitem();
                    item.setValue(plasticCard);
                    String pattern = "yyyy-MM-dd";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    item.appendChild(new Listcell(plasticCard.getCardNumber()));
                    item.appendChild(new Listcell(simpleDateFormat.format(plasticCard.getExpirationDate())));
                    item.appendChild(new Listcell(plasticCard.getCardHolder()));
                    item.appendChild(new Listcell(plasticCard.getCardStatusId().getDescription()));
                    item.appendChild(createButtonEditModal(plasticCard));
                    item.appendChild(createButtonViewModal(plasticCard));

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

    public void onClick$btnAssigment() throws InterruptedException {
        try {
            saveCard(plasticCard);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void saveCard(List<Card> list) {
        PlastiCustomizingRequestHasCard plastiCustomizingRequestHasCard = null;
        try {
            if (list != null && !list.isEmpty()) {
                for (Card plasticCard : list) {
                    plastiCustomizingRequestHasCard = new PlastiCustomizingRequestHasCard();
                    plastiCustomizingRequestHasCard.setCardId(plasticCard);
                    plastiCustomizingRequestHasCard.setPlasticCustomizingRequestId(plastiCustomerParam);
                    plastiCustomizingRequestHasCard.setCreateDate(new Timestamp(new Date().getTime()));
                    plastiCustomizingRequestHasCard = requestEJB.savePlastiCustomizingRequestHasCard(plastiCustomizingRequestHasCard);
                }
                this.showMessage("cms.common.msj.assignPlasticCard", false, null);
            }
        } catch (GeneralException ex) {
            Logger.getLogger(ListCardAssigmentControllers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullParameterException ex) {
            Logger.getLogger(ListCardAssigmentControllers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadCmbProduct(Integer evenInteger, Long programId) {
        cmbProduct.getItems().clear();
        EJBRequest request1 = new EJBRequest();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_PROGRAM_ID, programId);
        request1.setParams(params);
        List<Product> product;
        try {
            product = productEJB.getProductByProgram(request1);
            for (int i = 0; i < product.size(); i++) {
                Comboitem item = new Comboitem();
                item.setValue(product.get(i));
                item.setLabel(product.get(i).getName());
                item.setParent(cmbProduct);
            }
        } catch (EmptyListException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (GeneralException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (NullParameterException ex) {
            showError(ex);
            ex.printStackTrace();
        }
    }

    @Override
    public List<Card> getFilterList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
