package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Channel;
import com.cms.commons.models.ProgramLoyalty;
import com.cms.commons.models.ProgramLoyaltyTransaction;
import com.cms.commons.models.Transaction;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class AdminParametersController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;

    private Label lblLoyalty;
    private Combobox cmbChannel;
    private Combobox cmbTransaction;
    private Textbox txtTotal;
    private Textbox txtTotalMaximumTransactions;
    private Textbox txtTotalAmountDaily;
    private Textbox txtTotalAmountMonthly;
    private Textbox txtActivationCard;
    private Textbox txtRenovationCard;
    private ProductEJB productEJB = null;
    private ProgramEJB programEJB = null;
    private ProgramLoyaltyTransaction programLoyaltyTransactionParam;
    private Button btnSave;
    private Integer eventType;
    public Window winAdminParameters;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                programLoyaltyTransactionParam = (ProgramLoyaltyTransaction) Sessions.getCurrent().getAttribute("object");
                break;
            case WebConstants.EVENT_VIEW:
                programLoyaltyTransactionParam = (ProgramLoyaltyTransaction) Sessions.getCurrent().getAttribute("object");
                break;
            case WebConstants.EVENT_ADD:
                programLoyaltyTransactionParam = null;
                break;
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            programEJB = (ProgramEJB) EJBServiceLocator.getInstance().get(EjbConstants.PROGRAM_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtTotal.setRawValue(null);
        txtTotalMaximumTransactions.setRawValue(null);
        txtTotalAmountDaily.setRawValue(null);
        txtTotalAmountMonthly.setRawValue(null);
        txtActivationCard.setRawValue(null);
        txtRenovationCard.setRawValue(null);
    }

    private void loadField(ProgramLoyaltyTransaction programLoyaltyTransaction) {

        ProgramLoyalty programLoyalty = null;
        AdminLoyaltyController adminLoyalty = new AdminLoyaltyController();
        if (adminLoyalty.getProgramLoyaltyParent().getId() != null) {
            programLoyalty = adminLoyalty.getProgramLoyaltyParent();
        }
        lblLoyalty.setValue(programLoyalty.getDescription());
        
        if (programLoyaltyTransactionParam != null) {
            if (programLoyalty.getProgramLoyaltyTypeId().getId() == WebConstants.PROGRAM_LOYALTY_TYPE_POINT) {
                txtTotal.setText(programLoyaltyTransaction.getTotalPointsValue().toString());
                txtTotalMaximumTransactions.setText(programLoyaltyTransaction.getTotalMaximumTransactionsPoints().toString());
                txtTotalAmountDaily.setText(programLoyaltyTransaction.getTotalAmountDailyPoints().toString());
                txtTotalAmountMonthly.setText(programLoyaltyTransaction.getTotalAmountMonthlyPoints().toString());
                txtActivationCard.setText(programLoyaltyTransaction.getActivationCardPoints().toString());
                txtRenovationCard.setText(programLoyaltyTransaction.getRenovationCardPoints());
            } else if (programLoyalty.getProgramLoyaltyTypeId().getId() == WebConstants.PROGRAM_LOYALTY_TYPE_BONIFICATION) {
                txtTotal.setText(programLoyaltyTransaction.getTotalBonificationFixedValue().toString());
                txtTotalMaximumTransactions.setText(programLoyaltyTransaction.getTotalMaximumTransactionsBonification().toString());
                txtTotalAmountDaily.setText(programLoyaltyTransaction.getTotalAmountDailyBonification().toString());
                txtTotalAmountMonthly.setText(programLoyaltyTransaction.getTotalAmountMonthlyBonification().toString());
                txtActivationCard.setText(programLoyaltyTransaction.getActivationCardBonification().toString());
                txtRenovationCard.setText(programLoyaltyTransaction.getRenovationCardBonification().toString());
            }
        }
    }

    public void blockFields() {
        txtTotal.setReadonly(true);
        txtTotalMaximumTransactions.setReadonly(true);
        txtTotalAmountDaily.setReadonly(true);
        txtTotalAmountMonthly.setReadonly(true);
        txtActivationCard.setReadonly(true);
        txtRenovationCard.setReadonly(true);

        btnSave.setVisible(false);
    }

//    public Boolean validateEmpty() {
//        if (txtProduct.getText().isEmpty()) {
//            txtProduct.setFocus(true);
//            this.showMessage("sp.error.field.cannotNull", true, null);
//        } else {
//            return true;
//        }
//        return false;
//
//    }
    private void saveProgramLoyaltyTransactionParam(ProgramLoyaltyTransaction _programLoyaltyTransaction) {
        ProgramLoyalty programLoyalty = null;

        try {
            ProgramLoyaltyTransaction programLoyaltyTransaction = null;

            if (_programLoyaltyTransaction != null) {
                programLoyaltyTransaction = _programLoyaltyTransaction;
            } else {//New LegalPerson
                programLoyaltyTransaction = new ProgramLoyaltyTransaction();
            }

            //Loyalty
            AdminLoyaltyController adminLoyalty = new AdminLoyaltyController();
            if (adminLoyalty.getProgramLoyaltyParent().getId() != null) {
                programLoyalty = adminLoyalty.getProgramLoyaltyParent();
            }

            if (programLoyalty.getProgramLoyaltyTypeId().getId() == WebConstants.PROGRAM_LOYALTY_TYPE_POINT) {
                programLoyaltyTransaction.setChannelId((Channel) cmbChannel.getSelectedItem().getValue());
                programLoyaltyTransaction.setProgramLoyaltyId(programLoyalty);
                programLoyaltyTransaction.setTransactionId((Transaction) cmbTransaction.getSelectedItem().getValue());
                programLoyaltyTransaction.setTotalPointsValue(Float.parseFloat(txtTotal.getText()));
                programLoyaltyTransaction.setTotalMaximumTransactionsPoints(Float.parseFloat(txtTotalMaximumTransactions.getText()));
                programLoyaltyTransaction.setTotalAmountDailyPoints(Float.parseFloat(txtTotalAmountDaily.getText()));
                programLoyaltyTransaction.setTotalAmountMonthlyPoints(Float.parseFloat(txtTotalAmountMonthly.getText()));
                programLoyaltyTransaction.setActivationCardPoints(Float.parseFloat(txtActivationCard.getText()));
                programLoyaltyTransaction.setRenovationCardPoints(txtRenovationCard.getText());

            } else if (programLoyalty.getProgramLoyaltyTypeId().getId() == WebConstants.PROGRAM_LOYALTY_TYPE_BONIFICATION) {
                programLoyaltyTransaction.setChannelId((Channel) cmbChannel.getSelectedItem().getValue());
                programLoyaltyTransaction.setProgramLoyaltyId(programLoyalty);
                programLoyaltyTransaction.setTransactionId((Transaction) cmbTransaction.getSelectedItem().getValue());
                programLoyaltyTransaction.setTotalBonificationFixedValue(Float.parseFloat(txtTotal.getText()));
                programLoyaltyTransaction.setTotalMaximumTransactionsBonification(Float.parseFloat(txtTotalMaximumTransactions.getText()));
                programLoyaltyTransaction.setTotalAmountDailyBonification(Float.parseFloat(txtTotalAmountDaily.getText()));
                programLoyaltyTransaction.setTotalAmountMonthlyBonification(Float.parseFloat(txtTotalAmountMonthly.getText()));
                programLoyaltyTransaction.setActivationCardBonification(Float.parseFloat(txtActivationCard.getText()));
                programLoyaltyTransaction.setRenovationCardBonification(Float.parseFloat(txtRenovationCard.getText()));
            }
            programLoyaltyTransaction = programEJB.saveProgramLoyaltyTransaction(programLoyaltyTransaction);
            programLoyaltyTransactionParam = programLoyaltyTransaction;
            this.showMessage("sp.common.save.success", false, null);

            EventQueues.lookup("updateParameters", EventQueues.APPLICATION, true).publish(new Event(""));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
//        if (validateEmpty()) {
        switch (eventType) {
            case WebConstants.EVENT_ADD:
                saveProgramLoyaltyTransactionParam(null);
                break;
            case WebConstants.EVENT_EDIT:
                saveProgramLoyaltyTransactionParam(programLoyaltyTransactionParam);
                break;
            default:
                break;
        }
//        }
    }

    public void onClick$btnBack() {
        winAdminParameters.detach();
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadField(programLoyaltyTransactionParam);
                loadCmbTransaction(eventType);
                loadCmbChannel(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadField(programLoyaltyTransactionParam);
                blockFields();
                loadCmbTransaction(eventType);
                loadCmbChannel(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadField(programLoyaltyTransactionParam);
                loadCmbTransaction(eventType);
                loadCmbChannel(eventType);
                break;
            default:
                break;
        }
    }

    private void loadCmbTransaction(Integer evenInteger) {
        //cmbTransaction
        EJBRequest request1 = new EJBRequest();
        List<Transaction> transactions;
        try {
            transactions = productEJB.getTransaction(request1);
            loadGenericCombobox(transactions, cmbTransaction, "description", evenInteger, Long.valueOf(programLoyaltyTransactionParam != null ? programLoyaltyTransactionParam.getTransactionId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }

    private void loadCmbChannel(Integer evenInteger) {
        //cmbChannel
        EJBRequest request1 = new EJBRequest();
        List<Channel> channels;
        try {
            channels = productEJB.getChannel(request1);
            loadGenericCombobox(channels, cmbChannel, "name", evenInteger, Long.valueOf(programLoyaltyTransactionParam != null ? programLoyaltyTransactionParam.getChannelId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }
}