package com.example.grayscale3.DataHolders

object Flags {
    //Operations
    var Shipment: Boolean = false
    var IntakeFullKit: Boolean = false
    var AssignPatient: Boolean = false
    var AWD_NO: Boolean = false
    var GENERATE_CODE: Boolean =  false
    var FORMS: Boolean = false

    //Auth
    var TOKEN: String? = null
    var TOKEN_TYPE: String?= null

    //Barcode
    var BARCODE: String? = null
    var NAME_OR_EXPIRATION_OR_AWB: String? = null
    var BARCODE_TEXT_VIEW: String?= null
    var NAME_EXPIRATION_TEXT_VIEW: String?  = null

    //Generate Code
    var GENERATE_BARCODE_CODE: String? = null

    //City from country
    var CITY: String? = null

    //Laboratory from city
    var LAB: String? = null


    //No data
    var stopProgressBarIfNoLabsAreFetched: Boolean = false
    var stopProgressBarIfSocketTimeoutExceptionAccrued: Boolean = false
    var stopProgressBarIfNoPatientsLoaded: Boolean = false
    var stopProgressBarIfSocketTimeoutExceptionAccruedPatients: Boolean = false









}