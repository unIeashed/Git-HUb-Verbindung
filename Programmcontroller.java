package hendrich.palettenberechung.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * Programm Controller
 * 
 * @author stefan.hendrich
 * @Version Version 4
 * @since 15.0.1
 */
public class Programmcontroller implements Initializable {
	private PalettenProduktInhaltsliste tabelle = new PalettenProduktInhaltsliste();
	private Artikel artikel = new Artikel();
	private Verpackung verpackung = new Verpackung();
	private Paletten palette = new Paletten();
	private String[] artikeleListe = { "Name", "Nummer", "Gewicht" };
	private String[] verpackungListe = { "Name", "Gewicht", "Länge", "Breite", "Höhe", "Inhaltsmenge" };
	private String[] palettenListe = { "Name", "Gewicht", "Länge", "Breite", "Höhe", "Maximales_Beladegewicht",
			"Maximale_Ladehöhe" };

	private int zahlPal = -1;

	private File csvfilefolder;

	@FXML
	private MenuItem mi_Laden, mi_Speichern, mi_hinzufuegenArtikel, mi_hinzufuegenVperackung, mi_hinzufuegenPalette;

	@FXML
	private ListView<ObservableList<String>> listeArtikel, listeVerpackung, columArtikel;

	@FXML
	private ListView<ObservableList<Integer>> columMenge;

	@FXML
	private ListView<ObservableList<Float>> columGewicht, columHoehe, columLagen;

	@FXML
	private TextField tf_Stueckzahl;

	@FXML
	private Label lbl_columGewichtGesamt, lbl_columHoeheGesamt, lbl_columLagenGesamt, lbl_Gesamtgewicht;

	@FXML
	StackedBarChart<Integer, Integer> grafik;

	@FXML
	private Button btn_Gewichtberechnen, btn_Palettenvorschau, btn_zurueckzurUebersicht, btn_Drucken,
			btn_artikelentfernen, btn_verpackungentfernen, btn_Paletteentfernen, btn_artikelvonpaletteentfernen,
			btn_Mengeverringern, btn_Mengeerhoehen;

	@FXML
	private Pane p_Palettenuebersicht, p_palettengrafik;

	@FXML // hier werden die palettenliste in der Gui wiedergegeben
	private ChoiceBox<String> listePalette;

	@FXML // Label für die Hinzufügefenster
	private Label lbl_GewichtArtikel, lbl_NummerArtikel, lbl_InhaltsmengeVerpackung, lbl_GewichtVerpackung,
			lbl_LaengeVerpackung, lbl_BreiteVerpackung, lbl_HoeheVerpackung, lbl_GewichtPalette, lbl_LaengePalette,
			lbl_BreitePalette, lbl_HoehePalette, lbl_MaxbelastungPalette, lbl_MaxbeladehoehePalette;

	@FXML // Textfield für die Hinzufügefenster
	private TextField tf_NameArtikel, tf_NummerArtikel, tf_GewichtArtikel, tf_NameVerpackung, tf_InhaltsmengeVerpackung,
			tf_GewichtVerpackung, tf_LaengeVerpackung, tf_BreiteVerpackung, tf_HoeheVerpackung, tf_NamePalette,
			tf_GewichtPalette, tf_LaengePalette, tf_BreitePalette, tf_HoehePalette, tf_MaxbelastungPalette,
			tf_MaxBeladehoehePalette;

	@FXML // Button für die Hinzufügefenster
	private Button btn_HinzufuegenBestätigenArtikel, btn_HinzufuegenbeendenArtikel, btn_HinzufuegenBestätigenVerpackung,
			btn_hinzufuegenbeendenVerpackung, btn_HinzufuegenbeendenPalette, btn_HinzufuegenBestätigenPalette;

	@FXML // Pane um zwischen den Hinzufügefenstern wechseln zu können
	private Pane p_Artikelhinzufügenfensterunten, p_Verpackunghinzufügenfensterunten, p_Palettehinzufügenfensterunten,
			p_startFensterunten, p_Programmfenster;

	@FXML
	private Tooltip tooltipartikel;
	
	public Programmcontroller() {
	}
	
//	@FXML
//	protected void tooltip_Anzeigen(MouseEvent event) {
//		
//		int zahl = listeArtikel.getSelectionModel().getSelectedIndex();
//		Tooltip wert = new Tooltip();
//		wert.setText(artikel.getGewicht(zahl) + "");
//	}
	
	
	/**
	 * Zuerst wird geürüft, ob alle benötigten Vorgaben ausgewählt sind. Wenn dies
	 * der Fall ist, wird ein neuer Eintrag in die Produktinhaltsliste hinzugefügt
	 * und in der GuI enspr. ausgegeben.
	 * 
	 * @Version 1.0
	 */
	@FXML
	protected void gewichtberechnen(ActionEvent event) {
		int zahlArtikel = 0;
		int zahlVerp = 0;
		float lagen = 0;
		float hoehe = 0;
		try {
			if (zahlPal < 0) {
				zahlPal = listePalette.getSelectionModel().getSelectedIndex();
			}
			zahlArtikel = listeArtikel.getSelectionModel().getSelectedIndex();
			zahlVerp = listeVerpackung.getSelectionModel().getSelectedIndex();

			float zahl2Artikel = (artikel.getGewicht(zahlArtikel) * verpackung.getInhaltsmenge(zahlVerp))
					* (Float.parseFloat(tf_Stueckzahl.getText()));
			float zahl2Verp = verpackung.getGewicht(zahlVerp) * (Float.parseFloat(tf_Stueckzahl.getText()));
			Float berechnen = zahl2Artikel + zahl2Verp;
			System.out.println(berechnen);
			rundungen_ausrechnen_mit_Math_Round(berechnen);
			berechnen = berechnen / 1000;
			lagen = lagenberechnen(zahlVerp);
			rundungen_ausrechnen_mit_Math_Round(lagen);
			hoehe = hoeheberechenen(lagen, zahlVerp);
			rundungen_ausrechnen_mit_Math_Round(hoehe);

			new PalettenProduktInhaltsliste(artikel.getNamen(zahlArtikel), Integer.parseInt(tf_Stueckzahl.getText()),
					lagen, hoehe, berechnen);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Es wurde nicht alles ausgewählt, bitte einen Artikel, eine Verpackung, eine Palette auswählen, sowie die Menge angeben",
					"Berechnungsfehler!", 0, null);
		}
		columArtikel.getItems().clear();
		columMenge.getItems().clear();
		columLagen.getItems().clear();
		columHoehe.getItems().clear();
		columGewicht.getItems().clear();

		for (int i = 0; i < tabelle.getsize(); i++) {
			columArtikel.getItems().add(FXCollections.observableArrayList(tabelle.getName(i)));
			columMenge.getItems().add(FXCollections.observableArrayList(tabelle.getMenge(i)));
			columLagen.getItems().add(FXCollections.observableArrayList(tabelle.getLagen(i)));
			columHoehe.getItems().add(FXCollections.observableArrayList(tabelle.getHoehe(i)));
			columGewicht.getItems().add(FXCollections.observableArrayList(tabelle.getGewicht(i)));
		}

		if (!(columGewicht.getItems().isEmpty())) {
			paletten_gesamtgewicht_berechnen();
		}

	}

	/**
	 * Es wird das Gesamt Gewicht der Palette aus der ProduktInhaltsliste auf der
	 * Palette und des Nettogewichts der Palette selbst berechnet
	 * 
	 * @Version 1.0
	 */
	private void paletten_gesamtgewicht_berechnen() {
		float zahl2Pal = 0;
		float gesgewicht = 0;
		float gesLagen = 0;
		float geshoehe = 0;
		for (int i = 0; i < columGewicht.getItems().size(); i++) {
			gesgewicht = gesgewicht + tabelle.getGewicht(i);
			rundungen_ausrechnen_mit_Math_Round(gesgewicht);
			gesLagen = gesLagen + tabelle.getLagen(i);
			rundungen_ausrechnen_mit_Math_Round(gesLagen);
			geshoehe = geshoehe + tabelle.getHoehe(i);
			rundungen_ausrechnen_mit_Math_Round(geshoehe);
		}
		if (listePalette.getSelectionModel().isEmpty()) {
			zahl2Pal = palette.getGewicht(zahlPal);
		} else {
			zahlPal = listePalette.getSelectionModel().getSelectedIndex();
			zahl2Pal = palette.getGewicht(zahlPal);
		}
		try {
			if (gesgewicht > palette.getMaxbelastung(zahlPal)) {
				JOptionPane.showInternalMessageDialog(
						null, "Das Max. Beladegewicht der Palette ist um: "
								+ (gesgewicht - palette.getMaxbelastung(zahlPal)) + " kg überschritten",
						"Achtung! Beladegewicht Überschritten", 0);
				gesgewicht = gesgewicht - tabelle.getGewicht(tabelle.getsize() - 1);
				rundungen_ausrechnen_mit_Math_Round(gesgewicht);
				gesLagen = gesLagen - tabelle.getLagen(tabelle.getsize() - 1);
				rundungen_ausrechnen_mit_Math_Round(gesLagen);
				geshoehe = geshoehe - tabelle.getHoehe(tabelle.getsize() - 1);
				rundungen_ausrechnen_mit_Math_Round(geshoehe);
				tabelle.listeneneintragentfernen(tabelle.getsize() - 1);
				listenaktualisieren();
				lbl_columGewichtGesamt.setText(gesgewicht + " kg");
				lbl_Gesamtgewicht.setText(gesgewicht + zahl2Pal + "");
				lbl_columLagenGesamt.setText(gesLagen + "");
				lbl_columHoeheGesamt.setText(geshoehe + " cm");
			} else if (geshoehe > palette.getMaxbeladehoehe(zahlPal)) {
				JOptionPane.showInternalMessageDialog(
						null, "Die Max. Beladehöhe der Palette ist um: "
								+ (geshoehe - palette.getMaxbeladehoehe(zahlPal)) + " cm überschritten",
						"Achtung! Beladehöhe Überschritten", 0);
				geshoehe = geshoehe - tabelle.getHoehe(tabelle.getsize() - 1);
				tabelle.listeneneintragentfernen(tabelle.getsize() - 1);
				listenaktualisieren();
				lbl_columGewichtGesamt.setText(gesgewicht + " kg");
				lbl_Gesamtgewicht.setText(gesgewicht + zahl2Pal + "");
				lbl_columLagenGesamt.setText(gesLagen + "");
				lbl_columHoeheGesamt.setText(geshoehe + " cm");
			} else {
				lbl_columGewichtGesamt.setText(gesgewicht + " kg");
				lbl_Gesamtgewicht.setText(gesgewicht + zahl2Pal + "");
				lbl_columLagenGesamt.setText(gesLagen + "");
				lbl_columHoeheGesamt.setText(geshoehe + " cm");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * öffnet das Fenster was über den entsprechenden Menü-Button gerufen wird.
	 * 
	 * @param event wird zur Erkennung des Buttons mit "event.getTarget()" benötigt,
	 *              um zu unterscheiden, welches Fenster geöffnet werden soll.
	 * @Version 1.0
	 */
	@FXML // öffnet die enspr. Pane wenn einer der Menü Button geklickt wird
	protected void hinzufuegefenster_oeffnen(ActionEvent event) {

		if (event.getTarget() == mi_hinzufuegenArtikel) {
			fensterveraenderungen(p_Artikelhinzufügenfensterunten, false);
			fensterveraenderungen(p_startFensterunten, true);
			fensterveraenderungen(p_Verpackunghinzufügenfensterunten, true);
			fensterveraenderungen(p_Palettehinzufügenfensterunten, true);
		} else if (event.getTarget() == mi_hinzufuegenVperackung) {
			fensterveraenderungen(p_Verpackunghinzufügenfensterunten, false);
			fensterveraenderungen(p_startFensterunten, true);
			fensterveraenderungen(p_Palettehinzufügenfensterunten, true);
			fensterveraenderungen(p_Artikelhinzufügenfensterunten, true);
		} else if (event.getTarget() == mi_hinzufuegenPalette) {
			fensterveraenderungen(p_Palettehinzufügenfensterunten, false);
			fensterveraenderungen(p_startFensterunten, true);
			fensterveraenderungen(p_Artikelhinzufügenfensterunten, true);
			fensterveraenderungen(p_Verpackunghinzufügenfensterunten, true);

		} else {
			System.out.println("nichts passiert");
		}

	}

	/**
	 * Wird immer dann gerufen, wenn sich ein Fenster öffnen und ein anderes
	 * schließen soll.
	 * 
	 * @param p übergibt die Pane (Fenster)
	 * @param b übergibt des Warheitswert des Fensters (öffnen true, schliessen
	 *          false)
	 * @Version 1.0
	 */
	// dient zum wechseln der einzelnen Pane
	private void fensterveraenderungen(Pane p, boolean b) {
		if (b == true) {
			p.setDisable(true);
			p.setVisible(false);
		} else {
			p.setDisable(false);
			p.setVisible(true);
		}
	}

	/**
	 * Wechselt zwischen dem "Palettenüberichts-/Palettenvorschau-" Fenster durch
	 * klick auf den entsprechenden Button
	 * 
	 * @param event wird zur Erkennung des Buttons mit "event.getTarget()" benötigt,
	 *              um zu unterscheiden, welches Fenster angezeigt werden soll.
	 * @Version 1.0
	 */
	@FXML // wechselt zwischen paletetnübersicht und palettenvorchau Pane
	protected void palettnvorschausehen(ActionEvent event) {
		if (event.getTarget() == btn_Palettenvorschau) {
			fensterveraenderungen(p_palettengrafik, false);
			fensterveraenderungen(p_Palettenuebersicht, true);
		} else if (event.getTarget() == btn_zurueckzurUebersicht) {
			fensterveraenderungen(p_Palettenuebersicht, false);
			fensterveraenderungen(p_palettengrafik, true);

		}
	}

	/**
	 * Schliesst die Hinzufügefenster durch klick auf den entsprechenden Button
	 * 
	 * @param event wird zur Erkennung des Buttons mit "event.getTarget()" benötigt,
	 *              um zu unterscheiden, welches Fenster geschlossen werden soll.
	 * @Version 1.0
	 */
	@FXML
	protected void closeaddingfester(ActionEvent event) {
		if (event.getTarget() == btn_HinzufuegenbeendenArtikel) {
			fensterveraenderungen(p_Artikelhinzufügenfensterunten, true);
			fensterveraenderungen(p_startFensterunten, false);
		} else if (event.getTarget() == btn_hinzufuegenbeendenVerpackung) {
			fensterveraenderungen(p_Verpackunghinzufügenfensterunten, true);
			fensterveraenderungen(p_startFensterunten, false);
		} else if (event.getTarget() == btn_HinzufuegenbeendenPalette) {
			fensterveraenderungen(p_Palettehinzufügenfensterunten, true);
			fensterveraenderungen(p_startFensterunten, false);
		} else {
			System.out.println("nichts passiert");
		}
	}

	/**
	 * Es wird geprüft @see {@link #textfeldabfrage(TextField, Label)} und falls
	 * hier kein Fehler ensteht, wird eine neuer Artikel erstellt.
	 * 
	 * @Version 1.0
	 */
	@FXML
	protected void addArtikel(ActionEvent event) {
		textfeldabfrage(tf_NummerArtikel, lbl_NummerArtikel);
		textfeldabfrage(tf_GewichtArtikel, lbl_GewichtArtikel);
		if (tf_NameArtikel.getText().equals("")) {
			JOptionPane.showMessageDialog(null, "Es wurde kein Name eingegeben", "Fehler beim Hinzufügen", 0);
		} else {
			try {
				new Artikel(tf_NameArtikel.getText(), Integer.parseInt(tf_NummerArtikel.getText()),
						Integer.parseInt(tf_GewichtArtikel.getText()));
				listenaktualisieren();
				tf_NameArtikel.clear();
				tf_NummerArtikel.clear();
				tf_GewichtArtikel.clear();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * Es wird geprüft @see {@link #textfeldabfrage(TextField, Label)} und falls
	 * hier kein Fehler ensteht, wird eine neue Verpackung erstellt.
	 * 
	 * @Version 1.0
	 */
	@FXML
	protected void addVerpackung(ActionEvent event) {
		textfeldabfrage(tf_GewichtVerpackung, lbl_GewichtVerpackung);
		textfeldabfrage(tf_LaengeVerpackung, lbl_LaengeVerpackung);
		textfeldabfrage(tf_BreiteVerpackung, lbl_BreiteVerpackung);
		textfeldabfrage(tf_HoeheVerpackung, lbl_HoeheVerpackung);
		textfeldabfrage(tf_InhaltsmengeVerpackung, lbl_InhaltsmengeVerpackung);

		if (tf_NameVerpackung.getText().equals("")) {
			JOptionPane.showMessageDialog(null, "Es wurde kein Name eingegeben", "Fehler beim Hinzufügen", 0);
		} else {
			try {
				new Verpackung(tf_NameVerpackung.getText(), Integer.parseInt(tf_GewichtVerpackung.getText()),
						Integer.parseInt(tf_LaengeVerpackung.getText()),
						Integer.parseInt(tf_BreiteVerpackung.getText()), Integer.parseInt(tf_HoeheVerpackung.getText()),
						Integer.parseInt(tf_InhaltsmengeVerpackung.getText()));
				listenaktualisieren();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * Es wird geprüft @see {@link #textfeldabfrage(TextField, Label)} und falls
	 * hier kein Fehler ensteht, wird eine neue Palette erstellt.
	 * 
	 * @Version 1.0
	 */
	@FXML
	protected void addPalette(ActionEvent event) {
		textfeldabfrage(tf_GewichtPalette, lbl_GewichtPalette);
		textfeldabfrage(tf_LaengePalette, lbl_LaengePalette);
		textfeldabfrage(tf_BreitePalette, lbl_BreitePalette);
		textfeldabfrage(tf_HoehePalette, lbl_HoehePalette);
		textfeldabfrage(tf_MaxBeladehoehePalette, lbl_MaxbeladehoehePalette);
		textfeldabfrage(tf_MaxbelastungPalette, lbl_MaxbelastungPalette);

		if (tf_NamePalette.getText().equals("")) {
			JOptionPane.showMessageDialog(null, "Es wurde kein Name eingegeben", "Fehler beim Hinzufügen", 0);
		} else {
			try {
				new Paletten(tf_NamePalette.getText(), Float.parseFloat(tf_GewichtPalette.getText()),
						Integer.parseInt(tf_LaengePalette.getText()), Integer.parseInt(tf_BreitePalette.getText()),
						Integer.parseInt(tf_HoehePalette.getText()), Float.parseFloat(tf_MaxbelastungPalette.getText()),
						Integer.parseInt(tf_MaxBeladehoehePalette.getText()));
				listenaktualisieren();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * Es wird der Inhalt des Textfeldes abgefragt und gepfrüft, ob er mit den
	 * Vorgaben überein stimmt, falls dies nicht sein sollte, wird eine Fehler
	 * Nachricht erstellt.
	 * 
	 * @param tf  es wird das Textfeld übergeben
	 * @param lbl es wird das zugehörige Label mitübergeben
	 * @Version 1.0
	 */
	// prüft die textfelder nach der richigen Eingabe
	private void textfeldabfrage(TextField tf, Label lbl) {
		try {
			int zahl = Integer.parseInt(tf.getText());
			if (zahl < 0) {
				JOptionPane.showMessageDialog(null, "Die Zahl im Eingabefeld \"" + lbl.getText() + "\" ist negativ!",
						"Fehler beim Hinzufügen", 0);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Es wurde keine Zahl im Eingabefeld \"" + lbl.getText() + "\" eingegeben!",
					"Fehler beim Hinzufügen", 0);
		}

	}

	/**
	 * die Listen werden nach Änderungen (z.B. durch erstellen eines neuen Artikels)
	 * aktualisiert
	 * 
	 * @Version 1.0
	 */
	// dient nur zum aktualisieren der listen bei änderungen
	private void listenaktualisieren() {
		listeArtikel.getItems().clear();
		for (int i = 0; i < artikel.getArryListSize(); i++) {
			listeArtikel.getItems().add(FXCollections.observableArrayList(artikel.getNamen(i)));
		}
		listeVerpackung.getItems().clear();
		for (int i = 0; i < verpackung.getArrayListVerpackungSize(); i++) {
			listeVerpackung.getItems().add(FXCollections.observableArrayList(verpackung.getNamen(i)));
		}
		listePalette.getItems().clear();
		for (int i = 0; i < palette.getArrayListPalettenSize(); i++) {
			listePalette.getItems().add("Name: " + palette.getNamen(i) + ", Maximalbelastung: "
					+ palette.getMaxbelastung(i) + ", Maximalbeladehöhe: " + palette.getMaxbeladehoehe(i));
		}
		columArtikel.getItems().clear();
		columMenge.getItems().clear();
		columLagen.getItems().clear();
		columHoehe.getItems().clear();
		columGewicht.getItems().clear();
		for (int i = 0; i < tabelle.getsize(); i++) {
			columArtikel.getItems().add(FXCollections.observableArrayList(tabelle.getName(i)));
			columMenge.getItems().add(FXCollections.observableArrayList(tabelle.getMenge(i)));
			columLagen.getItems().add(FXCollections.observableArrayList(tabelle.getLagen(i)));
			columHoehe.getItems().add(FXCollections.observableArrayList(tabelle.getHoehe(i)));
			columGewicht.getItems().add(FXCollections.observableArrayList(tabelle.getGewicht(i)));
		}

	}

	/**
	 * Wird jedes mal gerufen, wenn ein Feld von der PalettenProduktInhaltsliste
	 * Selektiert wird.
	 * 
	 * @Version 1.0
	 */
	@FXML
	protected void markieren() {

		for (int i = 0; i < tabelle.getsize(); i++) {
			if (columArtikel.getSelectionModel().isSelected(i) && columMenge.getSelectionModel().isSelected(i)
					&& columGewicht.getSelectionModel().isSelected(i) && columLagen.getSelectionModel().isSelected(i)
					&& columHoehe.getSelectionModel().isSelected(i)) {
				columArtikel.getSelectionModel().clearSelection();
				columMenge.getSelectionModel().clearSelection();
				columLagen.getSelectionModel().clearSelection();
				columHoehe.getSelectionModel().clearSelection();
				columGewicht.getSelectionModel().clearSelection();
				System.out.println("ABC");
			}
			if (columArtikel.getSelectionModel().isSelected(i)) {
				System.out.println("columArtikel");
				columMenge.getSelectionModel().select(i);
				columLagen.getSelectionModel().select(i);
				columHoehe.getSelectionModel().select(i);
				columGewicht.getSelectionModel().select(i);
			} else if (columMenge.getSelectionModel().isSelected(i)) {
				System.out.println("columMenge");
				columArtikel.getSelectionModel().select(i);
				columLagen.getSelectionModel().select(i);
				columHoehe.getSelectionModel().select(i);
				columGewicht.getSelectionModel().select(i);
			} else if (columGewicht.getSelectionModel().isSelected(i)) {
				System.out.println("columGewicht");
				columMenge.getSelectionModel().select(i);
				columArtikel.getSelectionModel().select(i);
				columLagen.getSelectionModel().select(i);
				columHoehe.getSelectionModel().select(i);
			} else if (columLagen.getSelectionModel().isSelected(i)) {
				System.out.println("columLagen");
				columMenge.getSelectionModel().select(i);
				columArtikel.getSelectionModel().select(i);
				columGewicht.getSelectionModel().select(i);
				columHoehe.getSelectionModel().select(i);
			} else if (columHoehe.getSelectionModel().isSelected(i)) {
				System.out.println("columLagen");
				columMenge.getSelectionModel().select(i);
				columArtikel.getSelectionModel().select(i);
				columLagen.getSelectionModel().select(i);
				columGewicht.getSelectionModel().select(i);
			}

		}

	}

	/**
	 * Wird zum speichern von Informationen aus in die DatenBank (.XML) gerufen und
	 * erreicht dies durch Zugriff auf die Methoden der gerufenen Klassen Artikel,
	 * Verpackung, Paletten.
	 * 
	 * @Version 1.0
	 */
	@FXML
	protected void informationen_aus_allen_Listen_in_der_XML_DatenBank_speichern() {
		System.out.println("versuche zu schreiben");
		if (artikel.getArryListSize() == 0 && verpackung.getArrayListVerpackungSize() == 0
				&& palette.getArrayListPalettenSize() == 0) {
			JOptionPane.showMessageDialog(null, "Es gibt nichts zu Speichern!", "Speichervorgang", 0);
		} else {
			Element lager = new Element("Lager");
			Document doc = new Document(lager);
			Element artikelElement = new Element("Artikel");
			Element verpackungElement = new Element("Verpackung");
			Element paletteElement = new Element("Palette");

			artikel.schreiben(artikelElement, artikeleListe, doc);
			verpackung.schreiben(verpackungElement, verpackungListe, doc);
			palette.schreiben(paletteElement, palettenListe, doc);

			System.out.println("schreibversuch ausgeführt");
			XMLOutputter xmlOutput = null;

			try (FileOutputStream fos = new FileOutputStream("/Datenbank/LagerDB.xml")) {
				xmlOutput = new XMLOutputter();
				xmlOutput.setFormat(Format.getPrettyFormat());
				xmlOutput.output(doc, fos);
			} catch (Exception e) {
				System.out.println("Es gab einen Fehler");
				System.out.println(e.getMessage());
			}
			System.out.println(xmlOutput.outputString(doc));
			System.out.println("Datei gespeichert");
		}
	}

	/**
	 * Wird zum laden von Informationen aus der DatenBank (.XML) gerufen und
	 * erreicht dies durch Zugriff auf die Methoden der gerufenen Klassen Artikel,
	 * Verpackung, Paletten.
	 * 
	 * @Version 1.0
	 */
	@FXML
	protected void informationen_aus_der_XML_DatenBank_laden() {
		try {
			Element element = null;
			Document doc1 = null;
			File f = new File("/Datenbank/LagerDB.xml");

			SAXBuilder builder = new SAXBuilder();
			doc1 = builder.build(f);

			XMLOutputter fmt = new XMLOutputter();
			fmt.output(doc1, System.out);
			element = doc1.getRootElement();

			if (listeArtikel.getItems().size() < artikel.getArryListSize() || listeArtikel.getItems().isEmpty()) {
				artikel.lesen(element);
				System.out.println("ArtikelListe geladen");
			} else {
				System.out.println("Artikelliste wurde bereits geladen");
			}

			if (listeVerpackung.getItems().size() < verpackung.getArrayListVerpackungSize()
					|| listeVerpackung.getItems().isEmpty()) {
				verpackung.lesen(element);
				listenaktualisieren();
				System.out.println("Verpackungsliste geladen");
			} else {
				System.out.println("Verpackungsliste wurde bereits geladen");
			}

			if (listePalette.getItems().size() < palette.getArrayListPalettenSize()
					|| listePalette.getItems().isEmpty()) {
				palette.lesen(element);
				listenaktualisieren();
				System.out.println("Palettenliste geladen");
			} else {
				System.out.println("Palettenliste wurde bereits geladen");
			}
		} catch (JDOMException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	/**
	 * hier werden ja nach Button, die Selektierte Auswahl gelöscht.
	 * 
	 * @param event wird zur erkennung des Buttons mit "event.getTarget()" benötigt,
	 *              um zu unterscheiden, welche Selektierte Auswahl gelöscht werden
	 *              soll.
	 * @Version 1.0
	 */
	@FXML
	private void entfernen_Buttons_ausloesen(ActionEvent event) {
		if (event.getTarget() == btn_artikelentfernen) {
			int n = JOptionPane.showConfirmDialog(null, "Möchten Sie den Artikel wirklich entfernen?",
					"Artikel entfernen", JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.YES_OPTION) {
				if (listeArtikel.getSelectionModel().getSelectedIndex() >= 0) {
					artikel.listeneneintragentfernen(listeArtikel.getSelectionModel().getSelectedIndex());
				} else {
					JOptionPane.showMessageDialog(null, "Es ist kein Artikel ausgewählt", "Achtung!", 0, null);
				}
				listenaktualisieren();
			} else if (n == JOptionPane.NO_OPTION) {

			}
		}
		if (event.getTarget() == btn_verpackungentfernen) {
			int n = JOptionPane.showConfirmDialog(null, "Möchten Sie die Verpackung wirklich entfernen?",
					"Verpackung entfernen", JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.YES_OPTION) {
				if (listeVerpackung.getSelectionModel().getSelectedIndex() >= 0) {
					verpackung.listeneneintragentfernen(listeVerpackung.getSelectionModel().getSelectedIndex());
				} else {
					JOptionPane.showMessageDialog(null, "Es ist keine Verpackung ausgewählt", "Achtung!", 0, null);
				}
				listenaktualisieren();
			} else if (n == JOptionPane.NO_OPTION) {

			}
		}
		if (event.getTarget() == btn_Paletteentfernen) {
			int n = JOptionPane.showConfirmDialog(null, "Möchten Sie die Palette wirklich entfernen?",
					"Palette entfernen", JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.YES_OPTION) {
				if (listePalette.getSelectionModel().getSelectedIndex() >= 0) {
					palette.listeneneintragentfernen(listePalette.getSelectionModel().getSelectedIndex());
				} else {
					JOptionPane.showMessageDialog(null, "Es ist keine Palette ausgewählt", "Achtung!", 0, null);

				}
				listenaktualisieren();
			} else if (n == JOptionPane.NO_OPTION) {

			}
		}
		if (event.getTarget() == btn_artikelvonpaletteentfernen) {
			int n = JOptionPane.showConfirmDialog(null, "Möchten Sie den Artikel von der Palette wirklich entfernen?",
					"Artikel von Palette entfernen", JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.YES_OPTION) {
				if (columArtikel.getSelectionModel().getSelectedIndex() >= 0) {
					tabelle.listeneneintragentfernen(columArtikel.getSelectionModel().getSelectedIndex());
				} else {
					JOptionPane.showMessageDialog(null, "Es ist keine Produkt auf der Palette ausgewählt", "Achtung!",
							0, null);
				}
				listenaktualisieren();
				if (columArtikel.getSelectionModel().getSelectedIndex() >= 0) {
					paletten_gesamtgewicht_berechnen();
				}
			} else if (n == JOptionPane.NO_OPTION) {

			}
		}

	}

	/**
	 * Dient zur Veränderung der Menge des "Selektieren Produktes" auf der Palette
	 * durch das anklicken der "+" und "-" Buttons unterhalb der Mengenangaben Liste
	 * 
	 * @param event wird zur erkennung des Buttons mit "event.getTarget()" benötigt,
	 *              um zu unterscheiden ob die Menge des Produktes steigen oder
	 *              sinken soll.
	 * 
	 * @Version 1.0
	 */
	@FXML
	private void menge_des_Produktes_auf_derPalette_hoch_oder_runter_veraendern(ActionEvent event) {
		int menge = 0;
		float gewicht;
		float gewichtfuer1stueck = 0;
		float lagen;
		float lagenfuer1Stueck = 0;
		float hoehe;
		float hoehefuer1Lage = 0;
		int selectierterwert = columMenge.getSelectionModel().getSelectedIndex();
		if (columMenge.getSelectionModel().getSelectedIndex() >= 0) {
			menge = tabelle.getMenge(selectierterwert);
			gewicht = tabelle.getGewicht(columGewicht.getSelectionModel().getSelectedIndex());
			gewichtfuer1stueck = gewicht / menge;

			lagen = tabelle.getLagen(columLagen.getSelectionModel().getSelectedIndex());
			lagenfuer1Stueck = lagen / menge;

			hoehe = tabelle.getHoehe(columHoehe.getSelectionModel().getSelectedIndex());
			hoehefuer1Lage = (float) (hoehe / Math.ceil(lagen));
		} else {
			JOptionPane.showInternalMessageDialog(null,
					"bitte erst ein Produkt auswählen, um dessen Menge zu verändern", "Achtung!", 0);
		}
		try {
			if (event.getTarget() == btn_Mengeerhoehen) {
				tabelle.mengeaendern(selectierterwert, true);
			} else if (event.getTarget() == btn_Mengeverringern) {
				tabelle.mengeaendern(selectierterwert, false);
			}
		} catch (Exception e) {
			e.getMessage();
		}
		if (columMenge.getSelectionModel().getSelectedIndex() >= 0) {
			float neuberGewicht = gewichtfuer1stueck * tabelle.getMenge(selectierterwert);
			neuberGewicht = rundungen_ausrechnen_mit_Math_Round(neuberGewicht);
			tabelle.setGewicht(columGewicht.getSelectionModel().getSelectedIndex(), neuberGewicht);
			float neuberLagen = lagenfuer1Stueck * tabelle.getMenge(selectierterwert);
			neuberLagen = rundungen_ausrechnen_mit_Math_Round(neuberLagen);
			tabelle.setLagen(columLagen.getSelectionModel().getSelectedIndex(), neuberLagen);
			float neuberHoehe = (float) (hoehefuer1Lage * Math.ceil(tabelle.getLagen(selectierterwert)));
			neuberHoehe = rundungen_ausrechnen_mit_Math_Round(neuberHoehe);
			tabelle.setHoehe(columHoehe.getSelectionModel().getSelectedIndex(), neuberHoehe);
		}
		listenaktualisieren();
		columArtikel.getSelectionModel().select(selectierterwert);
		columMenge.getSelectionModel().select(selectierterwert);
		columLagen.getSelectionModel().select(selectierterwert);
		columHoehe.getSelectionModel().select(selectierterwert);
		columGewicht.getSelectionModel().select(selectierterwert);
		listePalette.getSelectionModel().select(zahlPal);
		if (columMenge.getSelectionModel().getSelectedIndex() >= 0) {
			paletten_gesamtgewicht_berechnen();
		}
	}

	/**
	 * Rundet alle Werte die hinein gegeben werden, auf 2 Stellen nach dem Komma
	 * 
	 * @param wert Übergabewert für die Berechnung der Math.Round Funktion
	 * 
	 * @retun float wert mit max. 2 stellen Nach dem Komma wird zurück gegeben
	 * @Version 1.0
	 */
	private float rundungen_ausrechnen_mit_Math_Round(Float wert) {
		float zahl = wert;
		zahl = Math.round(zahl * 1000);
		zahl = zahl / 1000;
		return zahl;

	}

	/**
	 * Berechnet die Anzahl der Lagen des Produktes, anhand der Verpackungsmasse und
	 * der Palettenmasse, welches der Palette hinzugefügt worden ist.
	 * 
	 * @param verpzahl gibt das Verpackungselement an
	 * @return gibt die Anzhal der Lagen zurück
	 *
	 * @Version 1.0
	 */
	private float lagenberechnen(int verpzahl) {
		float laenge = palette.getLaenge(zahlPal);
		float breite = palette.getBreite(zahlPal);
		float anzahlLaenge = (float) Math.floor(laenge / verpackung.getLaenge(verpzahl));
		float anzahlBreite = (float) Math.floor(breite / verpackung.getBreite(verpzahl));
		float mengenZahlproLage = anzahlLaenge * anzahlBreite;
		float menge = (int) Float.parseFloat(tf_Stueckzahl.getText());
		float berechnung = menge / mengenZahlproLage;
		berechnung = rundungen_ausrechnen_mit_Math_Round(berechnung);
		return berechnung;
	}

	/**
	 * Berechnen die Höhe des Prouktes, anhand der Lagen, welches der Palette
	 * hinzugefügt worden ist.
	 * 
	 * @param lagen    gibt die Lagenanzahl an.
	 * @param zahlVerp gibt das ausgewählte Verpackungselement an
	 * @return Gibt die berechnete Höhe des Produktes zurück
	 *
	 * @Version 1.0
	 */
	private float hoeheberechenen(float lagen, int zahlVerp) {
		float lageAnzahl = (float) Math.ceil(lagen);
		float geshoehe = (lageAnzahl * verpackung.getHoehe(zahlVerp)) / 10;
		return geshoehe;
	}

	/**
	 * Prüft, ob die Datenbank Datei vorhanden ist. Falls noch keine Datenbank
	 * vorhanden ist wird eine neue erstellt.
	 * 
	 * @Version 1.0
	 */
	public void dateipruefen() {
		try {
			File lagerDB = new File("/Datenbank/LagerDB.xml");
			if (lagerDB.createNewFile()) {
				System.out.println("Datei wurde erstellt");
			} else {
				System.out.println("Datei existiert bereits");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * prüft ob der Dateipfad existiert. Falls ja, wird geprüft, ob sich die XML
	 * Datei in diesem Ordner befindet. Falls nicht, wird der Ordner erstellt.
	 * 
	 * @Version 1.0
	 */
	public void dateipfadprüfen() {
		csvfilefolder = new File("/Datenbank/");
		if (csvfilefolder.isDirectory()) {
			if (csvfilefolder.list().length > 0) {
				System.out.println("Ordner ist nicht leer");
			} else {
				System.out.println("Ordner ist leer");
			}
		} else {
			System.out.println("Dies ist kein Ordner");
			csvfilefolder.mkdir();
			if (csvfilefolder.isDirectory()) {
				System.out.println("Ordner wurde erstellt");
			} else {
				System.out.println("Ordner erstellen fehlgeschlagen");
			}
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		dateipfadprüfen();
		dateipruefen();
		informationen_aus_der_XML_DatenBank_laden();
	}

}
