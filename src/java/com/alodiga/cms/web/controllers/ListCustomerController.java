package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.custom.components.ListcellEditButton;
import com.alodiga.cms.web.custom.components.ListcellViewButton;
import com.alodiga.cms.web.generic.controllers.GenericAbstractListController;
import static com.alodiga.cms.web.generic.controllers.GenericDistributionController.request;
import com.alodiga.cms.web.utils.Utils;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.LegalCustomer;
import com.cms.commons.models.NaturalCustomer;
import com.cms.commons.models.Person;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

public class ListCustomerController extends GenericAbstractListController<Person> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtRequestNumber;
    private PersonEJB personEJB = null;
    private List<Person> persons = null;
    private List<LegalCustomer> legalCustomerList = null;
    private List<NaturalCustomer> naturalCustomerList = null;
    public static int indAddRequestPerson;

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
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            getData();
            loadDataList(persons);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public int getAddRequestPerson() {
        return indAddRequestPerson;
    }

    public void onClick$btnDelete() {
    }

    public void loadDataList(List<Person> list) {
        String applicantName = "";
        NaturalCustomer naturalCustomer = null;
        LegalCustomer legalCustomer = null;
        try {
            lbxRecords.getItems().clear();
            Listitem item = null;
            if (list != null && !list.isEmpty()) {
                for (Person person : list) {
                    item = new Listitem();
                    item.setValue(person);
                    String pattern = "yyyy-MM-dd";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    item.appendChild(new Listcell(person.getCountryId().getName()));
                    item.appendChild(new Listcell(person.getPersonTypeId().getDescription()));
                    item.appendChild(new Listcell(simpleDateFormat.format(person.getCreateDate())));
                    if (person.getPersonTypeId().getIndNaturalPerson() == true) {
                        applicantName = person.getApplicantNaturalPerson().getFirstNames();
                        applicantName.concat(" ");
                        applicantName.concat(person.getApplicantNaturalPerson().getLastNames());
                        item.appendChild(new Listcell(applicantName));
                        adminPage = "TabNaturalPersonCustommer.zul";

                        EJBRequest request1 = new EJBRequest();
                        Map params = new HashMap();
                        params.put(Constants.PERSON_KEY, person.getId());
                        request1.setParams(params);
                        naturalCustomerList = personEJB.getNaturalCustomerByPerson(request1);
                        for (NaturalCustomer n : naturalCustomerList) {
                            naturalCustomer = n;
                        }

                        item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, naturalCustomer) : new Listcell());
                        item.appendChild(permissionRead ? new ListcellViewButton(adminPage, naturalCustomer) : new Listcell());
                    } else {
                        applicantName = person.getLegalPerson().getEnterpriseName();
                        item.appendChild(new Listcell(applicantName));
                        adminPage = "TabLegalPersonCustommer.zul";

                        EJBRequest request1 = new EJBRequest();
                        Map params = new HashMap();
                        params.put(Constants.PERSON_KEY, person.getId());
                        request1.setParams(params);
                        legalCustomerList = personEJB.getLegalCustomerByPerson(request1);
                        for (LegalCustomer n : legalCustomerList) {
                            legalCustomer = n;
                        }

                        item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, legalCustomer) : new Listcell());
                        item.appendChild(permissionRead ? new ListcellViewButton(adminPage, legalCustomer) : new Listcell());
                    }
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

    public void getData() {
        persons = new ArrayList<Person>();
        try {
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PERSON_CLASSIFICATION_KEY, Constants.PERSON_CLASSIFICATION_CUSTOMER);
            request1.setParams(params);
            persons = personEJB.getPersonByCustommer(request1);
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
        txtRequestNumber.setText("");
    }

    @Override
    public List<Person> getFilterList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
