package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.CardEJB;
import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.commons.exception.RegisterNotFoundException;
import com.alodiga.cms.web.custom.components.ListcellEditButton;
import com.alodiga.cms.web.custom.components.ListcellViewButton;
import com.alodiga.cms.web.generic.controllers.GenericAbstractListController;
import com.alodiga.cms.web.utils.Utils;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Card;
import com.cms.commons.models.Product;
import com.cms.commons.models.Program;
import com.cms.commons.models.RateByProduct;
import com.cms.commons.models.RateByProgram;
import com.cms.commons.models.RateByCard;
import com.cms.commons.models.Request;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.cms.commons.util.QueryConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

public class ListRateByCardController extends GenericAbstractListController<Request> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Combobox cmbProgram;
    private Combobox cmbProduct;
    private Combobox cmbCardHolders;
    private ProductEJB productEJB = null;
    private ProgramEJB programEJB = null;
    private CardEJB cardEJB = null;
    private List<RateByCard> rateByCardByCardList = new ArrayList<RateByCard>();
    private List<RateByProduct> rateByProductList = null;
    private RateByCard RateByCardParam;
    private Card product = null;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }

    public void startListener() {
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            //Evaluar Permisos  
            permissionEdit = true;
            permissionAdd = true;
            permissionRead = true;
            adminPage = "adminRateByProduct.zul";
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            programEJB = (ProgramEJB) EJBServiceLocator.getInstance().get(EjbConstants.PROGRAM_EJB);
            cardEJB = (CardEJB) EJBServiceLocator.getInstance().get(EjbConstants.CARD_EJB);
            eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
            loadCmbProgram(WebConstants.EVENT_ADD);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onChange$cmbProgram() {
        cmbCardHolders.setValue("");
        Program program = (Program) cmbProgram.getSelectedItem().getValue();
        loadCmbCardHolder(WebConstants.EVENT_ADD, program.getId());
    }
    
    public void onChange$cmbCardHolders() {
        cmbProduct.setValue("");
        Card card = (Card) cmbCardHolders.getSelectedItem().getValue();
        loadCmbProduct(WebConstants.EVENT_ADD, card.getCardHolder());
    }
    
    public void onChange$cmbProduct() {
        Card card = (Card) cmbProduct.getSelectedItem().getValue();
        getData(card.getProductId().getId());
    }

    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_ADD);
        Executions.getCurrent().sendRedirect("adminRateByProduct.zul");
    }

    public void onClick$btnViewRates() throws InterruptedException {
        product = (Card) cmbProduct.getSelectedItem().getValue();
        loadList(rateByProductList, product);
    }

    public void onClick$btnDelete() {
    }

    public void loadList(List<RateByProduct> list, Card productId) {
        List<RateByCard> rateByCardList = new ArrayList<RateByCard>();
        RateByCard rateByCard = null;
        EJBRequest request1 = new EJBRequest();
        Map params = new HashMap();
        int indLoadList = 0;
        String rbp1;
        int indExist = 0;
        try {
            params.put(QueryConstants.PARAM_PRODUCT_ID, product.getProductId().getId());
            request1.setParams(params);
            rateByCardByCardList = cardEJB.getRateByCardByCard(request1);
            if (rateByCardByCardList != null) {
                indLoadList = 1;                
                for (RateByCard r : rateByCardByCardList) {
                    rateByCardByCardList.add(r);
                }
                if (list != null && !list.isEmpty()) {
                    for (RateByProduct rp : list) {
                        rbp1 = rp.getChannelId().getId().toString() + rp.getTransactionId().getId().toString() + productId.getProgramId().getId().toString();
                        rateByCard = new RateByCard();
                        rateByCard.setCardId(product);
                        rateByCard.setChannelId(rp.getChannelId());
                        rateByCard.setFixedRate(rp.getFixedRate());
                        rateByCard.setPercentageRate(rp.getPercentageRate());
                        rateByCard.setRateApplicationTypeId(rp.getRateApplicationTypeId());
                        rateByCard.setTotalInitialTransactionsExempt(rp.getTotalInitialTransactionsExempt());
                        rateByCard.setTotalTransactionsExemptPerMonth(rp.getTotalTransactionsExemptPerMonth());
                        rateByCard.setTransactionId(rp.getTransactionId());
                        rateByCard = cardEJB.saveRateByCard(rateByCard);
                        rateByCardByCardList.add(rateByCard);
                    }
                }
            }
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (rateByCardByCardList != null && !rateByCardByCardList.isEmpty()) {
                for (RateByCard r : rateByCardByCardList) {
                    item = new Listitem();
                    item.setValue(r);
                    item.appendChild(new Listcell (r.getCardId().getProductId().getCountryId().getName()));
                    item.appendChild(new Listcell(r.getChannelId().getName()));
                    item.appendChild(new Listcell(r.getTransactionId().getDescription()));
                    item.appendChild(new Listcell(r.getFixedRate().toString()));
                    item.appendChild(new Listcell(r.getPercentageRate().toString()));
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, r) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, r) : new Listcell());
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
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (RegisterNotFoundException ex) {
            showError(ex);
        } finally {
            try {
                if (indLoadList == 0) {
                    lbxRecords.getItems().clear();
                    Listitem item = null;
                    if (list != null && !list.isEmpty()) {
                        for (RateByProduct rp : list) {
                            rateByCard = new RateByCard();
                            rateByCard.setCardId(product);
                            rateByCard.setChannelId(rp.getChannelId());
                            rateByCard.setFixedRate(rp.getFixedRate());
                            rateByCard.setPercentageRate(rp.getPercentageRate());
                            rateByCard.setRateApplicationTypeId(rp.getRateApplicationTypeId());
                            rateByCard.setTotalInitialTransactionsExempt(rp.getTotalInitialTransactionsExempt());
                            rateByCard.setTotalTransactionsExemptPerMonth(rp.getTotalTransactionsExemptPerMonth());
                            rateByCard.setTransactionId(rp.getTransactionId());
                            rateByCard = cardEJB.saveRateByCard(rateByCard);
                            rateByCardByCardList.add(rateByCard);
                        }
                        for (RateByCard r : rateByCardByCardList) {
                            item = new Listitem();
                            item.setValue(r);
                            item.appendChild(new Listcell(r.getCardId().getProductId().getCountryId().getName()));
                            item.appendChild(new Listcell(r.getChannelId().getName()));
                            item.appendChild(new Listcell(r.getTransactionId().getDescription()));
                            item.appendChild(new Listcell(r.getFixedRate().toString()));
                            item.appendChild(new Listcell(r.getPercentageRate().toString()));
                            item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, r) : new Listcell());
                            item.appendChild(permissionRead ? new ListcellViewButton(adminPage, r) : new Listcell());
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
                }
            } catch (RegisterNotFoundException ex) {
                showError(ex);
            } catch (NullParameterException ex) {
                showError(ex);
            } catch (GeneralException ex) {
                showError(ex);
            }
        }
    }

    public void getData(Long productId) {
        try {
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(QueryConstants.PARAM_PRODUCT_ID, productId);
            request1.setParams(params);
            rateByProductList = productEJB.getRateByProductByProduct(request1);
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
        item.appendChild(new Listcell());
        item.setParent(lbxRecords);
    }

    private void loadCmbProgram(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Program> programs;
        try {
            programs = programEJB.getProgram(request1);
            loadGenericCombobox(programs, cmbProgram, "name", evenInteger, Long.valueOf(0));
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

    private void loadCmbProduct(Integer evenInteger, String cardHolder) {
        cmbProduct.getItems().clear();
        EJBRequest request1 = new EJBRequest();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_CARDHOLDER, cardHolder);
        request1.setParams(params);
        List<Card> cardList;
        try {
            cardList = cardEJB.getCardByCardHolder(request1);
            for (int i = 0; i < cardList.size(); i++) {
                Comboitem item = new Comboitem();
                item.setValue(cardList.get(i));
                item.setLabel(cardList.get(i).getProductId().getName());
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
    
    private void loadCmbCardHolder(Integer evenInteger, long programId) {
        cmbCardHolders.getItems().clear();
        EJBRequest request1 = new EJBRequest();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_PROGRAM_ID, programId);
        request1.setParams(params);
        List<Card> cardByProgramList;
        try {
            cardByProgramList = cardEJB.getCardByProgram(request1);
            loadGenericCombobox(cardByProgramList, cmbCardHolders, "cardHolder", evenInteger, Long.valueOf(0));
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

    public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("cms.common.cardRequest.list"));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    @Override
    public List<Request> getFilterList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void loadDataList(List<Request> list) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void getData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
