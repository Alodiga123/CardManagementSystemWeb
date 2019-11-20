package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.custom.components.ListcellEditButton;
import com.alodiga.cms.web.custom.components.ListcellViewButton;
import com.alodiga.cms.web.generic.controllers.GenericAbstractListController;
import com.alodiga.cms.web.utils.Utils;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.Country;
import com.cms.commons.models.LegalPerson;
import com.cms.commons.models.Person;
import com.cms.commons.models.PhonePerson;
import com.cms.commons.models.State;
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

public class ListLegalPersonController extends GenericAbstractListController<Country> {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Textbox txtAlias;
    private UtilsEJB utilsEJB = null;
    private List<LegalPerson> legalPersons = null;
    private List<Person> persons = null;
    private List<PhonePerson> phonePersons = null;
    private List<Country> countries = null;
    private List<State> states = null;

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
            //
            
            adminPage = "adminLegalPerson.zul";
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            getData();
            loadList(legalPersons);
        } catch (Exception ex) {
            showError(ex);
        }
    }

//    public List<Country> getFilteredList(String filter) {
//        List<Country> countriesaux = new ArrayList<Country>();
//        Country country;
//        try {
//            if (filter != null && !filter.equals("")) {
//                country = utilsEJB.searchCountry(filter);
//                countriesaux.add(country);
//            } else {
//                return countries;
//            }
//        } catch (RegisterNotFoundException ex) {
//            Logger.getLogger(ListCountryController.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (Exception ex) {
//            showError(ex);
//        }
//        return countriesaux;
//    }

    public void onClick$btnAdd() throws InterruptedException {
        Sessions.getCurrent().setAttribute(WebConstants.EVENTYPE, WebConstants.EVENT_ADD);
        Executions.getCurrent().sendRedirect(adminPage);
    }

    public void onClick$btnDelete() {
    }

    public void loadList(List<LegalPerson> list) {
        try {
            lbxRecords.getItems().clear();
           /* Listitem item = null;
            if (list != null && !list.isEmpty()) {
                //btnDownload.setVisible(true);
                for (Country country : list) {
                    item = new Listitem();
                    item.setValue(country);
                    item.appendChild(new Listcell(country.getCode()));
                    item.appendChild(new Listcell(country.getName()));
                    item.appendChild(new Listcell(country.getCodeIso2()));
                    item.appendChild(new Listcell(country.getCodeIso2()));
                    item.appendChild(new Listcell(country.getCodeIso3()));
                    item.appendChild(new Listcell(country.getCodeIso3()));
                    item.appendChild(new Listcell(country.getCurrencyId().getName()));                    
                    item.appendChild(permissionEdit ? new ListcellEditButton(adminPage, country) : new Listcell());
                    item.appendChild(permissionRead ? new ListcellViewButton(adminPage, country) : new Listcell());
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
            }*/

        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void getData() {
        countries = new ArrayList<Country>();
       /* try {
            request.setFirst(0);
            request.setLimit(null);
            countries = utilsEJB.getCountries(request);
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
           showEmptyList();
        } catch (GeneralException ex) {
            showError(ex);
        }*/
    }
    
    
    private void showEmptyList(){
                Listitem item = new Listitem();
                item.appendChild(new Listcell(Labels.getLabel("sp.error.empty.list")));
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.appendChild(new Listcell());
                item.setParent(lbxRecords);  
    }

    public void onClick$btnDownload() throws InterruptedException {
        try {
            Utils.exportExcel(lbxRecords, Labels.getLabel("sp.bread.crumb.country.list"));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnClear() throws InterruptedException {
        txtAlias.setText("");
    }

//    public void onClick$btnSearch() throws InterruptedException {
//        try {
//            loadList(getFilteredList(txtAlias.getText()));
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }

    @Override
    public List<Country> getFilterList(String filter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void loadDataList(List<Country> list) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
