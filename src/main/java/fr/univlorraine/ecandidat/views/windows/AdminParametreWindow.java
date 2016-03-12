package fr.univlorraine.ecandidat.views.windows;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.entities.ecandidat.Parametre;
import fr.univlorraine.ecandidat.entities.ecandidat.Parametre_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.ParametrePresentation;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredStringCheckBox;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextArea;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextField;

/**
 * Fenêtre d'édition de parametre
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class AdminParametreWindow extends Window {
	
	/** serialVersionUID **/
	private static final long serialVersionUID = 5412661429668848283L;

	public static final String[] FIELDS_ORDER_STRING = {Parametre_.codParam.getName(), Parametre_.libParam.getName(),"valParamString"};
	public static final String[] FIELDS_ORDER_BOOLEAN = {Parametre_.codParam.getName(), Parametre_.libParam.getName(),"valParamBoolean"};
	public static final String[] FIELDS_ORDER_INTEGER = {Parametre_.codParam.getName(), Parametre_.libParam.getName(),"valParamInteger"};
	public String[] FIELDS_ORDER;

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient ParametreController parametreController;

	/* Composants */
	private CustomBeanFieldGroup<ParametrePresentation> fieldGroup;
	private Button btnEnregistrer;
	private Button btnAnnuler;

	/**
	 * Crée une fenêtre d'édition de parametre
	 * @param parametre la parametre à éditer
	 */
	public AdminParametreWindow(Parametre parametre) {
		ParametrePresentation parametrePres = new ParametrePresentation(parametre);
		if (parametre.getTypParam().equals(NomenclatureUtils.TYP_PARAM_BOOLEAN)){
			FIELDS_ORDER = FIELDS_ORDER_BOOLEAN;
		}else if (parametre.getTypParam().equals(NomenclatureUtils.TYP_PARAM_INTEGER)){
			FIELDS_ORDER = FIELDS_ORDER_INTEGER;
		}else if (parametre.getTypParam().startsWith(NomenclatureUtils.TYP_PARAM_STRING)){
			FIELDS_ORDER = FIELDS_ORDER_STRING;
		}
		/* Style */
		setModal(true);
		setWidth(500,Unit.PIXELS);
		setResizable(false);
		setClosable(false);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("parametre.window", null, UI.getCurrent().getLocale()));

		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(ParametrePresentation.class);
		fieldGroup.setItemDataSource(parametrePres);
		FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100,Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		for (String fieldName : FIELDS_ORDER) {
			String caption = applicationContext.getMessage("parametre.table." + fieldName, null, UI.getCurrent().getLocale());
			Field<?> field = null;
			if (fieldName.equals("valParamBoolean")){
				field = fieldGroup.buildAndBind(caption, fieldName,RequiredStringCheckBox.class);
			}else if (fieldName.equals(Parametre_.libParam.getName())){
				field = fieldGroup.buildAndBind(caption, fieldName,RequiredTextArea.class);
				field.setWidth(100, Unit.PERCENTAGE);
				//((RequiredTextArea)field).setRows(7);
			}
			else{
				field = fieldGroup.buildAndBind(caption, fieldName);
				field.setWidth(100,Unit.PERCENTAGE);
				if(fieldName.equals("valParamString")){
					((RequiredTextField)field).setNullRepresentation(null);
					Integer tailleMax = parametreController.getMaxLengthForString(parametre.getTypParam());
					field.addValidator(new StringLengthValidator(applicationContext.getMessage("parametre.taillemax.error", new Object[]{0,tailleMax}, UI.getCurrent().getLocale()), 0, tailleMax, true));
				}
				else if (fieldName.equals("valParamInteger") && parametrePres.getCodParam().equals(NomenclatureUtils.COD_PARAM_FILE_MAX_SIZE)){
					field.addValidator(value->{
						if (value==null){
							return;
						}
						Integer integerValue = null;
						try{
							integerValue = Integer.valueOf(value.toString());				
						}catch (Exception e){
							throw new InvalidValueException("");
						}
						Integer maxValue = ConstanteUtils.SIZE_MAX_PARAM_MAX_FILE_PJ;
						if (value!=null && integerValue>maxValue){
							throw new InvalidValueException(applicationContext.getMessage("parametre.taillemax.int.error", new Object[]{maxValue}, UI.getCurrent().getLocale()));
						}
					});
				}
			}
			formLayout.addComponent(field);
		}

		fieldGroup.getField(Parametre_.codParam.getName()).setReadOnly(true);
		fieldGroup.getField(Parametre_.libParam.getName()).setReadOnly(true);
		
		
		layout.addComponent(formLayout);

		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnAnnuler = new Button(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);

		btnEnregistrer = new Button(applicationContext.getMessage("btnSave", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
		btnEnregistrer.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnEnregistrer.addClickListener(e -> {
			try {				
				/* Valide la saisie */
				fieldGroup.commit();
				/* Enregistre le parametre saisie */
				parametreController.saveParametre(parametre,parametrePres);
				/* Ferme la fenêtre */
				close();
			} catch (CommitException ce) {
			}
		});
		buttonsLayout.addComponent(btnEnregistrer);
		buttonsLayout.setComponentAlignment(btnEnregistrer, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}

}
