package com.example.grayscale3.Classes

sealed class OperationState<T>(var barcode: String) {

    class Export<T>(barcode: String): OperationState<T>(barcode)
    class Import<T>(barcode: String): OperationState<T>(barcode)
    class Assign<T>(barcode: String): OperationState<T>(barcode)
    class AwbNo<T>(barcode: String): OperationState<T>(barcode)
    class GenerateBarcode<T>(barcode: String): OperationState<T>(barcode)
    class Forms<T>(barcode: String): OperationState<T>(barcode)


}