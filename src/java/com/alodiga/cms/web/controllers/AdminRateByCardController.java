package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.CardEJB;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.RateByProduct;
import com.cms.commons.models.RateByProgram;
import com.cms.commons.models.RateByCard;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
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
    private Float fixedRate;
    private Float percentageRate;
    private int totalTransactionInitialExempt;
    private int totalTransactionExemptPerMonth;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        rateByCardParam = (Sessions.getCurrent().getAttribute("object") != null) ? (RateByCard) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
           rateByCardParam = null;                    
       } else {
           rateByCardParam = (RateByCard) Sessions.getCurrent().getAttribute("object");            
       }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
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
            fixedRate = rateByCard.getFixedRateCR();
            percentageRate = rateByCard.getPercentageRateCR();
            totalTransactionInitialExempt = rateByCard.getTotalInitialTransactionsExemptCR();
            totalTransactionExemptPerMonth = rateByCard.getTotalTransactionsExemptPerMonthCR();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        btnSave.setVisible(false);
    }
    
    public void onChange$txtFixedRate() {
        this.clearMessage();
        if (Float.parseFloat(txtFixedRate.getText()) > fixedRate ) {
            this.showMessage("cms.rateByCard.Validation.fixedRate", false, null);
            btnSave.setDisabled(true);
        } else {
            this.clearMessage();
            btnSave.setDisabled(false);
        }
    }
    
    public void onChange$txtPercentageRate() {
        this.clearMessage();
        if (Float.parseFloat(txtPercentageRate.getText()) > percentageRate ) {
            this.showMessage("cms.rateByCard.Validation.percentageRate", false, null);
            btnSave.setDisabled(true);
        } else {
            this.clearMessage();
            btnSave.setDisabled(false);
        }
    }
    
    public void onChange$txtTotalTransactionInitialExempt() {
        this.clearMessage();
        if (Float.parseFloat(txtTotalTransactionInitialExempt.getText()) > totalTransactionInitialExempt ) {
            this.showMessage("cms.rateByCard.Validation.totalTransactionInitialExempt", false, null);
            btnSave.setDisabled(true);
        } else {
            this.clearMessage();
            btnSave.setDisabled(false);
        }
    }
    
    public void onChange$txtTotalTransactionExemptPerMonth() {
        this.clearMessage();
        if (Float.parseFloat(txtTotalTransactionExemptPerMonth.getText()) > totalTransactionExemptPerMonth ) {
            this.showMessage("cms.rateByCard.Validation.totalTransactionExemptPerMonth", false, null);
            btnSave.setDisabled(true);
        } else {
            this.clearMessage();
            btnSave.setDisabled(false);
        }
    }

    private void saveRateByCard(RateByCard _rateByCard) {
        try {
            boolean indModificationCardHolder = true;
            RateByCard rateByCard = null;

            if (_rateByCard != null) {
                rateByCard = _rateByCard;
            } else {
                rateByCard = new RateByCard();
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
            EventQueues.lookup("updateRateByCard", EventQueues.APPLICATION, true).publish(new Event(""));
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