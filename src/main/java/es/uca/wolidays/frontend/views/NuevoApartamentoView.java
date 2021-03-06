package es.uca.wolidays.frontend.views;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import es.uca.wolidays.backend.entities.Apartamento;
import es.uca.wolidays.backend.entities.Imagen;
import es.uca.wolidays.backend.entities.Ubicacion;
import es.uca.wolidays.backend.entities.Usuario;
import es.uca.wolidays.backend.security.SecurityUtils;
import es.uca.wolidays.backend.services.ApartamentoService;
import es.uca.wolidays.backend.services.UsuarioService;
import es.uca.wolidays.frontend.MainScreen;
import es.uca.wolidays.frontend.components.ImageUploader;

@Theme("wolidays")
@SpringView(name = NuevoApartamentoView.VIEW_NAME)
public class NuevoApartamentoView extends VerticalLayout implements View {
	
	private static final String CAMPO_OBLIGATORIO = "Campo obligatorio";
	private static final long serialVersionUID = -3089381541889114455L;
	public static final String VIEW_NAME = "nuevo_apartamento";
	
	@Autowired
	private transient UsuarioService userService;
	
	@Autowired
	private transient ApartamentoService aptoService;
	
	@Autowired
	MainScreen mainScreen;
	
	Binder<Apartamento> aptoBinder = new Binder<>();
	private String contactoRgx = "(?:[a-z0-9!#$%&'*+\\/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+"
			+ "\\/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-"
			+ "\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9]"
			+ "(?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"
			+ "|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|"
			+ "[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f"
			+ "\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])|(\\d{9})";
	private String precioStdRgx = "^\\d{0,5}(\\.\\d{1,2})?$";
	
	Binder<Ubicacion> ubiBinder = new Binder<>();
	private String ubicacionRgx = "[\\w\\s,.()áéíóúÁÉÍÓÚñÑ\\/\\º]+";
	
	private Boolean precioVacio = true;
	private Boolean precioValido = false;
	
	private Label title;

	@PostConstruct
	void init() {
		
		final VerticalLayout nuevoAptoLayout = new VerticalLayout();
		nuevoAptoLayout.setMargin(false);
		final HorizontalLayout fieldsLayout = new HorizontalLayout();
		final VerticalLayout leftFields = new VerticalLayout();
		final VerticalLayout rightFields = new VerticalLayout();
		Usuario currentUser = (Usuario)userService.loadUserByUsername(SecurityUtils.getUsername());
		
		title = new Label();
		title.setCaptionAsHtml(true);
		title.setCaption("<h1>Nuevo apartamento</h1>");
		
		TextField contactoField = new TextField("Contacto");
		contactoField.setIcon(VaadinIcons.INFO_CIRCLE);
		contactoField.setDescription("El contacto debe ser un email o un número de teléfono (9 dígitos)");
		aptoBinder.forField(contactoField)
			.withValidator(new RegexpValidator("El contacto debe ser un email o un número de teléfono", contactoRgx, true))
			.asRequired(CAMPO_OBLIGATORIO)
			.bind(Apartamento::getContacto, Apartamento::setContacto);
		
		TextField direccionField = new TextField("Dirección");
		ubiBinder.forField(direccionField)
			.withValidator(new RegexpValidator("La dirección debe ser válida", ubicacionRgx, true))
			.asRequired(CAMPO_OBLIGATORIO)
			.bind(Ubicacion::getDireccion, Ubicacion::setDireccion);
		
		TextField ciudadField = new TextField("Ciudad");
		ubiBinder.forField(ciudadField)
			.withValidator(new RegexpValidator("La ciudad debe ser válida", ubicacionRgx, true))
			.asRequired(CAMPO_OBLIGATORIO)
			.bind(Ubicacion::getCiudad, Ubicacion::setCiudad);
		
		TextField paisField = new TextField("País");
		ubiBinder.forField(paisField)
			.withValidator(new RegexpValidator("El país debe ser válido", ubicacionRgx, true))
			.asRequired(CAMPO_OBLIGATORIO)
			.bind(Ubicacion::getPais, Ubicacion::setPais);		
		
		List<Integer> data = IntStream.range(1, 11).mapToObj(i -> i).collect(Collectors.toList());
		
        NativeSelect<Integer> numCamasField = new NativeSelect<>("Número de camas", data);
        numCamasField.setEmptySelectionAllowed(false);
        numCamasField.setSelectedItem(data.get(0));
		numCamasField.setWidth("50px");
		aptoBinder.forField(numCamasField)
			.asRequired(CAMPO_OBLIGATORIO)
			.bind(Apartamento::getNumCamas, Apartamento::setNumCamas);
		
		NativeSelect<Integer> numDormitoriosField = new NativeSelect<>("Número de dormitorios", data);
        numDormitoriosField.setEmptySelectionAllowed(false);
        numDormitoriosField.setSelectedItem(data.get(0));
		numDormitoriosField.setWidth("50px");
		aptoBinder.forField(numDormitoriosField)
			.asRequired(CAMPO_OBLIGATORIO)
			.bind(Apartamento::getNumDormitorios, Apartamento::setNumDormitorios);
		
		CheckBox aireAcondCB = new CheckBox("Aire acondicionado", false);
		aptoBinder.forField(aireAcondCB)
			.bind(Apartamento::getAireAcondicionado, Apartamento::setAireAcondicionado);
		
		leftFields.addComponents(contactoField, direccionField, ciudadField, paisField, numCamasField, numDormitoriosField, aireAcondCB);
		leftFields.setComponentAlignment(contactoField, Alignment.TOP_LEFT);
		leftFields.setComponentAlignment(direccionField, Alignment.TOP_LEFT);
		leftFields.setComponentAlignment(ciudadField, Alignment.TOP_LEFT);
		leftFields.setComponentAlignment(paisField, Alignment.TOP_LEFT);
		leftFields.setComponentAlignment(numCamasField, Alignment.TOP_LEFT);
		leftFields.setComponentAlignment(numDormitoriosField, Alignment.TOP_LEFT);
		leftFields.setComponentAlignment(aireAcondCB, Alignment.TOP_LEFT);
		
		TextArea descripcionField = new TextArea("Descripción");
		descripcionField.setWidth("300px");
		aptoBinder.forField(descripcionField)
			.bind(Apartamento::getDescripcion, Apartamento::setDescripcion);		
		
		TextField precioStdField = new TextField("Precio estándar por noche");
		precioStdField.setIcon(VaadinIcons.INFO_CIRCLE);
		precioStdField.setDescription("Precio que valdrá una noche en tu apartamento. Más tarde podrás definir otros precios para fechas concretas. Debes separar los decimales con un punto (.)");
		precioStdField.setWidth("85px");
		aptoBinder.forField(precioStdField)
			.asRequired(CAMPO_OBLIGATORIO);
		
		ImageUploader imageUploader = new ImageUploader("Arrastra las fotos de tu apartamento aquí");
		imageUploader.setWidth(100, Unit.PERCENTAGE);
		
		rightFields.addComponents(descripcionField, precioStdField);
		rightFields.setComponentAlignment(descripcionField, Alignment.TOP_LEFT);
		rightFields.setComponentAlignment(precioStdField, Alignment.TOP_LEFT);
		
		fieldsLayout.addComponents(leftFields,rightFields);
		fieldsLayout.setComponentAlignment(leftFields, Alignment.MIDDLE_CENTER);
		fieldsLayout.setComponentAlignment(rightFields, Alignment.MIDDLE_CENTER);
		fieldsLayout.setWidth("40%");
		
		Button registrarAptoButton = new Button("Registrar apartamento");
		registrarAptoButton.addClickListener(e -> {
			Apartamento apartamento = new Apartamento();
			Ubicacion ubicacion = new Ubicacion();
			
			apartamento.setPropietario(currentUser);
			
			if (!precioStdField.isEmpty()) {
				precioVacio = false;
				
				String precioStd = precioStdField.getValue();
				Pattern patt = Pattern.compile(precioStdRgx);
				Matcher mat = patt.matcher(precioStd);
				if(mat.matches()) {
					precioValido = true;
				}
			}
			
			if(precioVacio) {
				Notification.show("Debes establecer un precio estandar");
			} else if(!precioValido) {
				Notification.show("El precio debe contener solo números, no empezar por 0 y los decimales deben estar separados con un punto (.)");
			} else {
			
				try {
					ubiBinder.writeBean(ubicacion);
					apartamento.setUbicacion(ubicacion);
					Set<Imagen> imagenesApto = imageUploader.getImages();
					for(Imagen i : imagenesApto)
						aptoService.guardarImagen(i);
						
					
					apartamento.setImagenes(imagenesApto);
					
					apartamento.setPrecioEstandar(Double.parseDouble(precioStdField.getValue()));
					aptoBinder.writeBean(apartamento);
					aptoService.guardar(apartamento);
	
					MisApartamentosView.setSuccessfulNuevoAptoNotification();
					getUI().getNavigator().navigateTo(MisApartamentosView.VIEW_NAME);
					
				} catch(ValidationException ex) {
					Notification.show("No se ha podido completar el registro");
				} catch (NumberFormatException nex) {
					Notification.show("El precio debe contener solo números y los decimales deben estar separados con un punto (.)");
				}
			}
		});
		registrarAptoButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		
		nuevoAptoLayout.addComponents(title, fieldsLayout, imageUploader, registrarAptoButton);
		nuevoAptoLayout.setComponentAlignment(title, Alignment.TOP_CENTER);
		nuevoAptoLayout.setComponentAlignment(fieldsLayout, Alignment.TOP_CENTER);
		nuevoAptoLayout.setComponentAlignment(imageUploader, Alignment.TOP_CENTER);
		nuevoAptoLayout.setComponentAlignment(registrarAptoButton, Alignment.TOP_CENTER);
		
		addComponent(nuevoAptoLayout);
	}
	
	
	@Override
	public void enter(ViewChangeEvent event) {
		mainScreen.setButtons();
		
		precioVacio = true;
		precioValido = false;
	}
	
}
