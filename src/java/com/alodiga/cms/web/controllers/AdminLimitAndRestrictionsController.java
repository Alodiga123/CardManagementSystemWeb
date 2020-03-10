package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Channel;
import com.cms.commons.models.Product;
import com.cms.commons.models.ProductHasChannelHasTransaction;
import com.cms.commons.models.ProductUse;
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

public class AdminLimitAndRestrictionsController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;

    private Combobox cmbProductUse;
    private Combobox cmbTransaction;
    private Combobox cmbChannel;
    private Label txtProduct;
    private Textbox txtMaxNumbTransDaily;
    private Textbox txtMaxNumbTransMont;
    private Textbox txtAmountMinTransDomestic;
    private Textbox txtAmountMaxTransDomestic;
    private Textbox txtAmountMinTransInt;
    private Textbox txtAmountMaxTransInt;
    private Textbox txtDailyAmountLimitDomestic;
    private Textbox txtMonthlyAmountLimitDomestic;
    private Textbox txtDailyAmountLimitInt;
    private Textbox txtMonthlyAmountLimitInt;
    private ProductEJB productEJB = null;
    private ProductHasChannelHasTransaction productHasChannelHasTransactionParam;
    private Button btnSave;
    private Integer eventType;
    public Window winAdminLimitAndRestrictions;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                productHasChannelHasTransactionParam = (ProductHasChannelHasTransaction) Sessions.getCurrent().getAttribute("object");
                break;
            case WebConstants.EVENT_VIEW:
                productHasChannelHasTransactionParam = (ProductHasChannelHasTransaction) Sessions.getCurrent().getAttribute("object");
                break;
            case WebConstants.EVENT_ADD:
                productHasChannelHasTransactionParam = null;
                break;
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtMaxNumbTransDaily.setRawValue(null);
        txtMaxNumbTransMont.setRawValue(null);
        txtAmountMinTransDomestic.setRawValue(null);
        txtAmountMaxTransDomestic.setRawValue(null);
        txtAmountMinTransInt.setRawValue(null);
        txtAmountMaxTransInt.setRawValue(null);
        txtDailyAmountLimitDomestic.setRawValue(null);
        txtMonthlyAmountLimitDomestic.setRawValue(null);
        txtDailyAmountLimitInt.setRawValue(null);
        txtMonthlyAmountLimitInt.setRawValue(null);
    }

    private void loadFields(ProductHasChannelHasTransaction productHasChannelHasTransaction) {
        try {
            txtMaxNumbTransDaily.setText(productHasChannelHasTransaction.getMaximumNumberTransactionsDaily().toString());
            txtMaxNumbTransMont.setText(productHasChannelHasTransaction.getMaximumNumberTransactionsMonthly().toString());
            txtAmountMinTransDomestic.setText(productHasChannelHasTransaction.getAmountMinimumTransactionDomestic().toString());
            txtAmountMaxTransDomestic.setText(productHasChannelHasTransaction.getAmountMaximumTransactionDomestic().toString());
            txtAmountMinTransInt.setText(productHasChannelHasTransaction.getAmountMinimumTransactionInternational().toString());
            txtAmountMaxTransInt.setText(productHasChannelHasTransaction.getAmountMaximumTransactionInternational().toString());
            txtDailyAmountLimitDomestic.setText(productHasChannelHasTransaction.getDailyAmountLimitDomestic().toString());
            txtMonthlyAmountLimitDomestic.setText(productHasChannelHasTransaction.getMonthlyAmountLimitDomestic().toString());
            txtDailyAmountLimitInt.setText(productHasChannelHasTransaction.getDailyAmountLimitInternational().toString());
            txtMonthlyAmountLimitInt.setText(productHasChannelHasTransaction.getMonthlyAmountLimitInternational().toString());
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    
    private void loadField(ProductHasChannelHasTransaction productHasChannelHasTransaction) {
        Product product = null;
        AdminProductController adminProduct = new AdminProductController();
        if (adminProduct.getProductParent().getId() != null) {
            product = adminProduct.getProductParent();
        }
        txtProduct.setValue(product.getName());
    }

    public void blockFields() {
        txtMaxNumbTransDaily.setReadonly(true);
        txtMaxNumbTransMont.setReadonly(true);
        txtAmountMinTransDomestic.setReadonly(true);
        txtAmountMaxTransDomestic.setReadonly(true);
        txtAmountMinTransInt.setReadonly(true);
        txtAmountMaxTransInt.setReadonly(true);
        txtDailyAmountLimitDomestic.setReadonly(true);
        txtMonthlyAmountLimitDomestic.setReadonly(true);
        txtDailyAmountLimitInt.setReadonly(true);
        txtMonthlyAmountLimitInt.setReadonly(true);
        btnSave.setVisible(false);
    }

    private void saveProductHasChannelHasTransaction(ProductHasChannelHasTransaction _productHasChannelHasTransaction) {
        Product product = null;
        try {
            ProductHasChannelHasTransaction productHasChannelHasTransaction = null;

            if (_productHasChannelHasTransaction != null) {
                productHasChannelHasTransaction = _productHasChannelHasTransaction;
            } else {//New LegalPerson
                productHasChannelHasTransaction = new ProductHasChannelHasTransaction();
            }

            //Product
            AdminProductController adminProduct = new AdminProductController();
            if (adminProduct.getProductParent().getId() != null) {
                product = adminProduct.getProductParent();
            }

            productHasChannelHasTransaction.setMaximumNumberTransactionsDaily(Integer.parseInt(txtMaxNumbTransDaily.getText()));
            productHasChannelHasTransaction.setMaximumNumberTransactionsMonthly(Integer.parseInt(txtMaxNumbTransMont.getText()));
            productHasChannelHasTransaction.setAmountMinimumTransactionDomestic(Float.parseFloat(txtAmountMinTransDomestic.getText()));
            productHasChannelHasTransaction.setAmountMaximumTransactionDomestic(Float.parseFloat(txtAmountMaxTransDomestic.getText()));
            productHasChannelHasTransaction.setAmountMinimumTransactionInternational(Float.parseFloat(txtAmountMinTransInt.getText()));
            productHasChannelHasTransaction.setAmountMaximumTransactionInternational(Float.parseFloat(txtAmountMaxTransInt.getText()));
            productHasChannelHasTransaction.setDailyAmountLimitDomestic(Float.parseFloat(txtDailyAmountLimitDomestic.getText()));
            productHasChannelHasTransaction.setMonthlyAmountLimitDomestic(Float.parseFloat(txtMonthlyAmountLimitDomestic.getText()));
            productHasChannelHasTransaction.setDailyAmountLimitInternational(Float.parseFloat(txtDailyAmountLimitInt.getText()));
            productHasChannelHasTransaction.setMonthlyAmountLimitInternational(Float.parseFloat(txtMonthlyAmountLimitInt.getText()));
            productHasChannelHasTransaction.setProductUseId((ProductUse) cmbProductUse.getSelectedItem().getValue());
            productHasChannelHasTransaction.setTransactionId((Transaction) cmbTransaction.getSelectedItem().getValue());
            productHasChannelHasTransaction.setChannelId((Channel) cmbChannel.getSelectedItem().getValue());
            productHasChannelHasTransaction.setProductId(product);
            productHasChannelHasTransaction = productEJB.saveProductHasChannelHasTransaction(productHasChannelHasTransaction);
            productHasChannelHasTransactionParam = productHasChannelHasTransaction;
            this.showMessage("sp.common.save.success", false, null);

            EventQueues.lookup("updateLimitAndRestrictions", EventQueues.APPLICATION, true).publish(new Event(""));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
//        if (validateEmpty()) {
        switch (eventType) {
            case WebConstants.EVENT_ADD:
                saveProductHasChannelHasTransaction(null);
                break;
            case WebConstants.EVENT_EDIT:
                saveProductHasChannelHasTransaction(productHasChannelHasTransactionParam);
                break;
            default:
                break;
        }
//        }
    }

    public void onClick$btnBack() {
        winAdminLimitAndRestrictions.detach();
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(productHasChannelHasTransactionParam);
                loadField(productHasChannelHasTransactionParam);
                //txtProduct.setReadonly(true);
                loadCmbProductUse(eventType);
                loadCmbTransaction(eventType);
                loadCmbChannel(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(productHasChannelHasTransactionParam);
                loadField(productHasChannelHasTransactionParam);
                blockFields();
                loadCmbProductUse(eventType);
                loadCmbTransaction(eventType);
                loadCmbChannel(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadField(productHasChannelHasTransactionParam);
                loadCmbProductUse(eventType);
                loadCmbTransaction(eventType);
                loadCmbChannel(eventType);
                break;
            default:
                break;
        }
    }

    private void loadCmbProductUse(Integer evenInteger) {
        //cmbProductUse
        EJBRequest request1 = new EJBRequest();
        List<ProductUse> productUses;
        try {
            productUses = productEJB.getProductUse(request1);
            loadGenericCombobox(productUses, cmbProductUse, "description", evenInteger, Long.valueOf(productHasChannelHasTransactionParam != null ? productHasChannelHasTransactionParam.getProductUseId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }

    private void loadCmbTransaction(Integer evenInteger) {
        //cmbTransaction
        EJBRequest request1 = new EJBRequest();
        List<Transaction> transactions;
        try {
            transactions = productEJB.getTransaction(request1);
            loadGenericCombobox(transactions, cmbTransaction, "description", evenInteger, Long.valueOf(productHasChannelHasTransactionParam != null ? productHasChannelHasTransactionParam.getTransactionId().getId() : 0));
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
            loadGenericCombobox(channels, cmbChannel, "name", evenInteger, Long.valueOf(productHasChannelHasTransactionParam != null ? productHasChannelHasTransactionParam.getChannelId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }
}
