package com.alodiga.cms.web.controllers;

import com.cms.commons.models.User;
import com.cms.commons.util.Constants;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

public class AdminMainMenuController extends GenericForwardComposer {

    private static final long serialVersionUID = -9145887024839938515L;
    Listcell ltcFullName;
    Listcell ltcProfile;
    Listcell ltcLogin;
    private static String OPTION = "option";
    private static String OPTION_NONE = "none";
    private static String OPTION_CUSTOMERS_LIST = "ltcCustomerList";
    private Listbox lbxPermissions;
    private Long languageId;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initialize();
    }

    public void initialize() {
        try {
            loadAccountData();

            checkOption();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void checkOption() {

    }

    private void loadAccountData() {
        User user = (User) session.getAttribute(Constants.USER_OBJ_SESSION);
        ltcFullName.setLabel(user.getFirstNames() + " " + user.getLastNames());
        //TODO:
        ltcProfile.setLabel("Administrador");
        ltcLogin.setLabel(user.getLogin());

        //Opcion RequestType         
        Listitem item = new Listitem();
        Listcell listCell = new Listcell();
        listCell.setLabel(Labels.getLabel("cms.menu.requestType.list"));;
        listCell.addEventListener("onClick", new RedirectListener("listRequestType.zul"));
        listCell.setParent(item);
        item.setParent(lbxPermissions);

        // Opcion Country
        Listitem item2 = new Listitem();
        Listcell listCell2 = new Listcell();
        listCell2.setLabel(Labels.getLabel("sp.common.country"));
        listCell2.addEventListener("onClick", new RedirectListener("listCountry.zul"));
        listCell2.setParent(item2);
        item2.setParent(lbxPermissions);

        /// Opcion Currency
        Listitem item3 = new Listitem();
        Listcell listCell3 = new Listcell();
        listCell3.setLabel(Labels.getLabel("sp.common.currency"));
        listCell3.addEventListener("onClick", new RedirectListener("listCurrency.zul"));
        listCell3.setParent(item3);
        item3.setParent(lbxPermissions);

        // Opcion statusCard
         Listitem item4 = new Listitem();
         Listcell listCell4 = new Listcell();
         listCell4.setLabel(Labels.getLabel("sp.common.statusCard"));
         listCell4.addEventListener("onClick", new RedirectListener("listCardStatus.zul"));
         listCell4.setParent(item4);
         item4.setParent(lbxPermissions);

         /// Opction status
         Listitem item5 = new Listitem();
         Listcell listCell5 = new Listcell();
         listCell5.setLabel(Labels.getLabel("cms.common.statusRequest"));
         listCell5.addEventListener("onClick", new RedirectListener("listStatusRequest.zul"));
         listCell5.setParent(item5);
         item5.setParent(lbxPermissions);
        
        // Opction collectionRequest
        Listitem item6 = new Listitem();
        Listcell listCell6 = new Listcell();
        listCell6.setLabel(Labels.getLabel("cms.common.collectionRequest"));
        listCell6.addEventListener("onClick", new RedirectListener("listCollectionRequest.zul"));
        listCell6.setParent(item6);
        item6.setParent(lbxPermissions);

        //Option Classification_Person
         Listitem item7 = new Listitem();
         Listcell listCell7 = new Listcell();
         listCell7.setLabel(Labels.getLabel("sp.commom.Classification.Person"));
         listCell7.addEventListener("onClick", new RedirectListener("listClassificationPerson.zul"));
         listCell7.setParent(item7);
         item7.setParent(lbxPermissions);

         // Option State
         Listitem item8 = new Listitem();
         Listcell listCell8 = new Listcell();
         listCell8.setLabel(Labels.getLabel("cms.common.name.state"));
         listCell8.addEventListener("onClick", new RedirectListener("listState.zul"));
         listCell8.setParent(item8);
         item8.setParent(lbxPermissions);
        
        //Option CollectionTypes
        Listitem item9 = new Listitem();
        Listcell listCell9 = new Listcell();
        listCell9.setLabel(Labels.getLabel("cms.requestCollection.collectionTypes"));
        listCell9.addEventListener("onClick", new RedirectListener("listCollectionTypes.zul"));
        listCell9.setParent(item9);
        item9.setParent(lbxPermissions);

        // Option Program
        Listitem item10 = new Listitem();
        Listcell listCell10 = new Listcell();
        listCell10.setLabel(Labels.getLabel("cms.common.Program"));
        listCell10.addEventListener("onClick", new RedirectListener("listProgram.zul"));
        listCell10.setParent(item10);
        item10.setParent(lbxPermissions);

        // Option Request
        Listitem item11 = new Listitem();
        Listcell listCell11 = new Listcell();
        listCell11.setLabel(Labels.getLabel("cms.menu.request.list"));
        listCell11.addEventListener("onClick", new RedirectListener("listRequest.zul"));
        listCell11.setParent(item11);
        item11.setParent(lbxPermissions);

        // Option Transaction
        Listitem item12 = new Listitem();
        Listcell listCell12 = new Listcell();
        listCell12.setLabel(Labels.getLabel("cms.menu.transactions"));
        listCell12.addEventListener("onClick", new RedirectListener("listTransaction.zul"));
        listCell12.setParent(item12);
        item12.setParent(lbxPermissions);

        // Option Issuer
        Listitem item13 = new Listitem();
        Listcell listCell13 = new Listcell();
        listCell13.setLabel(Labels.getLabel("cms.menu.card.issuer"));
        listCell13.addEventListener("onClick", new RedirectListener("listIssuer.zul"));
        listCell13.setParent(item13);
        item13.setParent(lbxPermissions);

        // Option Loyalty
        Listitem item14 = new Listitem();
        Listcell listCell14 = new Listcell();
        listCell14.setLabel(Labels.getLabel("cms.menu.loyalty"));
        listCell14.addEventListener("onClick", new RedirectListener("listLoyalty.zul"));
        listCell14.setParent(item14);
        item14.setParent(lbxPermissions);

        /*// Option PhoneType
         Listitem item15 = new Listitem();
         Listcell listCell15 = new Listcell();
         listCell15.setLabel(Labels.getLabel("cms.menu.phoneType.list"));
         listCell15.addEventListener("onClick", new RedirectListener("listPhoneType.zul"));
         listCell15.setParent(item15);
         item15.setParent(lbxPermissions);

         // Option PersonType
         Listitem item16 = new Listitem();
         Listcell listCell16 = new Listcell();
         listCell16.setLabel(Labels.getLabel("cms.menu.personType.list"));
         listCell16.addEventListener("onClick", new RedirectListener("listPersonType.zul"));
         listCell16.setParent(item16);
         item16.setParent(lbxPermissions);

         // Option DocumentsPersonType
         Listitem item17 = new Listitem();
         Listcell listCell17 = new Listcell();
         listCell17.setLabel(Labels.getLabel("cms.menu.documentspersonType.list"));
         listCell17.addEventListener("onClick", new RedirectListener("listDocumentsPersonType.zul"));
         listCell17.setParent(item17);
         item17.setParent(lbxPermissions);*/
        
        // Option Product
        Listitem item18 = new Listitem();
        Listcell listCell18 = new Listcell();
        listCell18.setLabel(Labels.getLabel("cms.menu.product"));
        listCell18.addEventListener("onClick", new RedirectListener("listProduct.zul"));
        listCell18.setParent(item18);
        item18.setParent(lbxPermissions);

        //Opción General Rates
        Listitem item19 = new Listitem();
        Listcell listCell19 = new Listcell();
        listCell19.setLabel(Labels.getLabel("cms.menu.generalRate.list"));
        listCell19.addEventListener("onClick", new RedirectListener("TabGeneralRates.zul"));
        listCell19.setParent(item19);
        item19.setParent(lbxPermissions);

        //Opción Account Manager
        Listitem item20 = new Listitem();
        Listcell listCell20 = new Listcell();
        listCell20.setLabel(Labels.getLabel("cms.menu.accountManager.list"));
        listCell20.addEventListener("onClick", new RedirectListener("listAccountProperties.zul"));
        listCell20.setParent(item20);
        item20.setParent(lbxPermissions);

        //Opción List Customer
        Listitem item21 = new Listitem();
        Listcell listCell21 = new Listcell();
        listCell21.setLabel(Labels.getLabel("cms.menu.customer.list"));
        listCell21.addEventListener("onClick", new RedirectListener("listCustomer.zul"));
        listCell21.setParent(item21);
        item21.setParent(lbxPermissions);
        
        //Opción PlasticManufacturer
        Listitem item22 = new Listitem();
        Listcell listCell22 = new Listcell();
        listCell22.setLabel(Labels.getLabel("cms.menu.plasticManufacturer"));
        listCell22.addEventListener("onClick", new RedirectListener("listPlasticManufacturer.zul"));
        listCell22.setParent(item22);
        item22.setParent(lbxPermissions);

        //Opción Account Type
        Listitem item23 = new Listitem();
        Listcell listCell23 = new Listcell();
        listCell23.setLabel(Labels.getLabel("cms.menu.accountType"));
        listCell23.addEventListener("onClick", new RedirectListener("listAccountType.zul"));
        listCell23.setParent(item23);
        item23.setParent(lbxPermissions);

        //Opción Sub Account Type
        Listitem item24 = new Listitem();
        Listcell listCell24 = new Listcell();
        listCell24.setLabel(Labels.getLabel("cms.menu.subAccountType"));
        listCell24.addEventListener("onClick", new RedirectListener("listSubAccountType.zul"));
        listCell24.setParent(item24);
        item24.setParent(lbxPermissions);
        
        //Opción Card Assignment
        Listitem item26 = new Listitem();
        Listcell listCell26 = new Listcell();
        listCell26.setLabel(Labels.getLabel("cms.menu.cardAssigment"));
        listCell26.addEventListener("onClick", new RedirectListener("listCardAssigment.zul"));
        listCell26.setParent(item26);
        item26.setParent(lbxPermissions);
        
        //Opción User
        Listitem item25 = new Listitem();
        Listcell listCell25 = new Listcell();
        listCell25.setLabel(Labels.getLabel("cms.menu.user"));
        listCell25.addEventListener("onClick", new RedirectListener("listUser.zul"));
        listCell25.setParent(item25);
        item25.setParent(lbxPermissions);
        
        //Opción plastic request
        Listitem item28 = new Listitem();
        Listcell listCell28 = new Listcell();
        listCell28.setLabel(Labels.getLabel("cms.common.menu.plasticRequest"));
        listCell28.addEventListener("onClick", new RedirectListener("listPlasticRequest.zul"));
        listCell28.setParent(item28);
        item28.setParent(lbxPermissions);
        
        //Opción Card Manager
        Listitem item29 = new Listitem();
        Listcell listCell29 = new Listcell();
        listCell29.setLabel(Labels.getLabel("cms.menu.cardManager"));
        listCell29.addEventListener("onClick", new RedirectListener("listCardManager.zul"));
        listCell29.setParent(item29);
        item29.setParent(lbxPermissions);

        //Opción Rates By Program
        Listitem item30 = new Listitem();
        Listcell listCell30 = new Listcell();
        listCell30.setLabel(Labels.getLabel("cms.menu.rateByProgram.list"));
        listCell30.addEventListener("onClick", new RedirectListener("TabRatesByProgram.zul"));
        listCell30.setParent(item30);
        item30.setParent(lbxPermissions);

        //Opción Rates By Product
        Listitem item31 = new Listitem();
        Listcell listCell31 = new Listcell();
        listCell31.setLabel(Labels.getLabel("cms.menu.rate.product"));
        listCell31.addEventListener("onClick", new RedirectListener("listRateByProduct.zul"));
        listCell31.setParent(item31);
        item31.setParent(lbxPermissions);
        
         //Opción RateByCardHolder
        Listitem item32 = new Listitem();
        Listcell listCell32 = new Listcell();
        listCell32.setLabel(Labels.getLabel("cms.menu.rateByCard.list"));
        listCell32.addEventListener("onClick", new RedirectListener("listRateByCard.zul"));
        listCell32.setParent(item32);
        item32.setParent(lbxPermissions);
        
        //Opción Card Program Manager 
        Listitem item43 = new Listitem();
        Listcell listCell43 = new Listcell();
        listCell43.setLabel(Labels.getLabel("cms.menu.card.program.manager"));
        listCell43.addEventListener("onClick", new RedirectListener("listCardProgramManager.zul"));
        listCell43.setParent(item43);
        item43.setParent(lbxPermissions);
        
        //Opción Permission 
        Listitem item44 = new Listitem();
        Listcell listCell44 = new Listcell();
        listCell44.setLabel(Labels.getLabel("cms.menu.permission"));
        listCell44.addEventListener("onClick", new RedirectListener("listPermission.zul"));
        listCell44.setParent(item44);
        item44.setParent(lbxPermissions);
        
        //Opción Permission Data
        Listitem item45 = new Listitem();
        Listcell listCell45 = new Listcell();
        listCell45.setLabel(Labels.getLabel("cms.menu.permission.data"));
        listCell45.addEventListener("onClick", new RedirectListener("listPermissionData.zul"));
        listCell45.setParent(item45);
        item45.setParent(lbxPermissions);
        
        //Opción Permission Group
        Listitem item46 = new Listitem();
        Listcell listCell46 = new Listcell();
        listCell46.setLabel(Labels.getLabel("cms.menu.permission.group"));
        listCell46.addEventListener("onClick", new RedirectListener("listPermissionGroup.zul"));
        listCell46.setParent(item46);
        item46.setParent(lbxPermissions);
        
        //Opción Permission Group Data
        Listitem item47 = new Listitem();
        Listcell listCell47 = new Listcell();
        listCell47.setLabel(Labels.getLabel("cms.menu.permission.group.data"));
        listCell47.addEventListener("onClick", new RedirectListener("listPermissionGroupData.zul"));
        listCell47.setParent(item47);
        item47.setParent(lbxPermissions);
    }
}

class RedirectListener implements EventListener {

    private String view = null;
    private Long permissionId = null;

    public RedirectListener() {
    }

    public RedirectListener(String view, Long permissionId) {
        this.view = view;
        this.permissionId = permissionId;
    }

    public RedirectListener(String view) {
        this.view = view;
    }

    @Override
    public void onEvent(Event event) throws UiException, InterruptedException {

        Executions.sendRedirect(view);

    }
}
