package de.franky.l.capricornng

/**
 * Created by franky on 08.06.2019.
 * Stores the data for the widget elements to fill them via loop
 */
internal class NG_DispData {

    var icon: Int = 0                                            // Speichert die R-Werte der Icons im Array um per Index zugreifen zu koennen
    var value:  String? = null                                   // Speichert den Anzeigewert fürs Widget
    var unit:   String? = null                                   // Speichert den Anzeigewert für die Einheit fürs Widget
    var number: Double  = 0.toDouble()                           // Speichert den Wert als Zahl für weitere Berechnungnen

    init {
        icon = 12343
        value = "tbd"
        unit = "tbd"
        number = 0.0
    }
}
