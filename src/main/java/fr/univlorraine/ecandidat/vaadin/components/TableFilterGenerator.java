package fr.univlorraine.ecandidat.vaadin.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.tepi.filtertable.FilterGenerator;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.controllers.TypeDecisionController;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatut_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement_;
import fr.univlorraine.ecandidat.vaadin.form.LocalDateField;

/** Génerateur de filtres
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class TableFilterGenerator implements FilterGenerator {

	/**serialVersionUID**/
	private static final long serialVersionUID = 1L;

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient TypeDecisionController typeDecisionController;
	
	/*Listener indiquant qu'il y a eu un changement dans le layout*/
	private FilterListener filterListener;
	
	/**
	 * @see org.tepi.filtertable.FilterGenerator#generateFilter(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Filter generateFilter(Object propertyId, Object value) {
		return null;
	}

	/**
	 * @see org.tepi.filtertable.FilterGenerator#generateFilter(java.lang.Object, com.vaadin.ui.Field)
	 */
	@Override
	public Filter generateFilter(Object propertyId, Field<?> originatingField) {		
		// TODO Auto-generated method stub
		//System.out.println(propertyId+" "+originatingField);
		return null;
	}

	/**
	 * @see org.tepi.filtertable.FilterGenerator#getCustomFilterComponent(java.lang.Object)
	 */
	@Override
    public AbstractField<?> getCustomFilterComponent(Object propertyId) {
		List<String> list = new ArrayList<String>();
        if (propertyId.equals(Candidature_.typeTraitement.getName()+"."+TypeTraitement_.libTypTrait.getName())) {
            tableRefController.getListeTypeTraitement().forEach(e->list.add(e.getLibTypTrait()));
            return generateComboBox(list);
        }else  if (propertyId.equals(Candidature_.typeStatut.getName()+"."+TypeStatut_.libTypStatut.getName())) {            
            tableRefController.getListeTypeStatut().forEach(e->list.add(e.getLibTypStatut()));      
            return generateComboBox(list);
        }else  if (propertyId.equals("lastTypeDecision."+TypeDecisionCandidature_.typeDecision.getName()+"."+TypeDecision_.libTypDec.getName())) {
            typeDecisionController.getTypeDecisionsEnService().forEach(e->list.add(e.getLibTypDec()));
            return generateComboBox(list);
        }else if (propertyId.equals(Candidature_.datReceptDossierCand.getName()) || propertyId.equals(Candidature_.datTransDossierCand.getName())
        		|| propertyId.equals(Candidature_.datCompletDossierCand.getName()) || propertyId.equals(Candidature_.datIncompletDossierCand.getName())){
        	return new LocalDateField();
        }
        return null;
    }
	
	/**
	 * @param liste
	 * @return une combo grace a la liste
	 */
	private ComboBox generateComboBox(List<String> liste){
		ComboBox sampleIdCB = new ComboBox();
        BeanItemContainer<String> dataList = new BeanItemContainer<String>(String.class);
        dataList.addBean(applicationContext.getMessage("filter.all", null, UI.getCurrent().getLocale()));
        dataList.addAll(liste);
        sampleIdCB.setNullSelectionItemId(applicationContext.getMessage("filter.all", null, UI.getCurrent().getLocale()));            
        sampleIdCB.setContainerDataSource(dataList);            
        sampleIdCB.setImmediate(true);
        return sampleIdCB;
	}

	/**
	 * @see org.tepi.filtertable.FilterGenerator#filterRemoved(java.lang.Object)
	 */
	@Override
	public void filterRemoved(Object propertyId) {		
		if (filterListener!=null){
			filterListener.removeFilter(propertyId);
		}
	}

	/**
	 * @see org.tepi.filtertable.FilterGenerator#filterAdded(java.lang.Object, java.lang.Class, java.lang.Object)
	 */
	@Override
	public void filterAdded(Object propertyId,
			Class<? extends Filter> filterType, Object value) {
		if (filterListener!=null){
			filterListener.addFilter(propertyId, filterType, value);
		}
	}

	/**
	 * @see org.tepi.filtertable.FilterGenerator#filterGeneratorFailed(java.lang.Exception, java.lang.Object, java.lang.Object)
	 */
	@Override
	public Filter filterGeneratorFailed(Exception reason, Object propertyId,
			Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Défini le 'Listener' utilisé
	 * @param filterListener
	 */
	public void addFilterListener(FilterListener filterListener) {
		this.filterListener = filterListener;
	}

	/**
	 * Interface pour récupérer le moment ou le filtre est modifié
	 */
	public interface FilterListener extends Serializable {

		/** Appelé lorsqu'un filtre est ajouté
		 * @param propertyId
		 * @param filterType
		 * @param value
		 */
		public void addFilter(Object propertyId,
				Class<? extends Filter> filterType, Object value);
		
		/** Appelé lorsqu'un filtre est supprimé
		 * @param propertyId
		 */
		public void removeFilter(Object propertyId);

	}
}
