package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.CardEJB;
import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.RateByProduct;
import com.cms.commons.models.RateByProgram;
import com.cms.commons.models.RateByCard;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;

import org.zkoss.zul.Toolbarbutton;

public class AdminRateByCardController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private RateByCard rateByCardParam;
    public static RateByProgram rateProgram = null;
    public static RateByProduct rateProduct = null;
    private CardEJB cardEJB = null;
    private Label lblProgram;
    private Label lblProductType;
    private Label lblProduct;
    private Label lblChannel;
    private Label lblTransaction;
    private Label lblRateApplicationType;
    private Textbox txtFixedRate;
    private Textbox txtPercentageRate;
    private Textbox txtTotalTransactionInitialExempt;
    private Textbox txtTotalTransactionExemptPerMonth;
    private Button btnSave;
    private Toolbarbutton tbbTitle;
    public Tabbox tb;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        rateByCardParam = (Sessions.getCurrent().getAttribute("object") != null) ? (RateByCard) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.rateByCard.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.rateByCard.view"));
                break;
            default:
                break;
        }
        try {
            cardEJB = (CardEJB) EJBServiceLocator.getInstance().get(EjbConstants.CARD_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public RateByProduct getRateByProduct() {
        return rateProduct;
    }
    
    public void clearFields() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void loadFields(RateByCard rateByCard) {
        try {
            lblProgram.setValue(rateByCard.getCardId().getProductId().getProgramId().getName());
            lblProductType.setValue(rateByCard.getCardId().getProductId().getProgramId().getProductTypeId().getName());
            lblProduct.setValue(rateByCard.getCardId().getProductId().getName());
            lblChannel.setValue(rateByCard.getChannelId().getName());
            lblTransaction.setValue(rateByCard.getTransactionId().getDescription());
            lblRateApplicationType.setValue(rateByCard.getRateApplicationTypeId().getDescription());
            txtFixedRate.setText(rateByCard.getFixedRate().toString());
            txtPercentageRate.setText(rateByCard.getPercentageRate().toString());
            txtTotalTransactionInitialExempt.setText(rateByCard.getTotalInitialTransactionsExempt().toString());
            txtTotalTransactionExemptPerMonth.setText(rateByCard.getTotalTransactionsExemptPerMonth().toString());            
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        btnSave.setVisible(false);
    }

    private void saveRateByCard(RateByCard _rateByCard) {
        try {
            boolean indModificationCardHolder = true;
            RateByCard rateByCard = null;

            if (_rateByCard != null) {
                rateByCard = _rateByCard;
            } else {//New Request
                rateByCard = new RateByCard();
            }
            
            //Validar que los valores modificados no sean mayores que las que tienes las tarifas del producto
            if (Float.parseFloat(txtFixedRate.getText()) > rateByCard.getFixedRate()) {
                this.showMessage("sp.common.msjValidateRatenAmounts", false, null);
            }
            
            //Guarda las tarifas del producto en la BD
            rateByCard.setChannelId(rateByCard.getChannelId());
            rateByCard.setTransactionId(rateByCard.getTransactionId());
            rateByCard.setFixedRate(Float.parseFloat(txtFixedRate.getText()));
            rateByCard.setPercentageRate(Float.parseFloat(txtPercentageRate.getText()));
            rateByCard.setTotalInitialTransactionsExempt(Integer.parseInt(txtTotalTransactionInitialExempt.getText()));
            rateByCard.setTotalTransactionsExemptPerMonth(Integer.parseInt(txtTotalTransactionExemptPerMonth.getText()));
            rateByCard.setRateApplicationTypeId(rateByCard.getRateApplicationTypeId());
            rateByCard = cardEJB.saveRateByCard(rateByCard);
            rateByCardParam = rateByCard;
            this.showMessage("sp.common.save.success", false, null);
        } catch (Exception ex) {
            showError(ex);
        }

    }

    public void onClick$btnSave() {
        switch (eventType) {
            case WebConstants.EVENT_ADD:
                saveRateByCard(null);
                break;
            case WebConstants.EVENT_EDIT:
                saveRateByCard(rateByCardParam);
                break;
            default:
                break;
        }
    }
    
    public void onclick$btnBack() {
        Executions.getCurrent().sendRedirect("listRateByProduct.zul");
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(rateByCardParam);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(rateByCardParam);
                txtFixedRate.setDisabled(true);
                txtPercentageRate.setDisabled(true);
                txtTotalTransactionInitialExempt.setDisabled(true);
                txtTotalTransactionExemptPerMonth.setDisabled(true);
                blockFields();
                break;
            default:
                break;
        }
    }
    
}