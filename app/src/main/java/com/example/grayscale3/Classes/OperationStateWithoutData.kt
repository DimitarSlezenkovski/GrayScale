package com.example.grayscale3.Classes

sealed class OperationStateWithoutData {
    object Export: OperationStateWithoutData()
    object Import: OperationStateWithoutData()
    object Assign: OperationStateWithoutData()
    object AwbNo: OperationStateWithoutData()
    object GenerateCode: OperationStateWithoutData()
}