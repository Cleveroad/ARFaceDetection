package com.packagename.models.converters

import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.SingleTransformer

interface Converter<IN, OUT> {
    /**
     * Convert IN to OUT
     *
     * @param inObject object ot convertIterableToList
     * @return converted object
     */
    fun convertInToOut(inObject: IN): OUT

    fun convertInToOutRx(inObject: IN): Flowable<OUT>

    /**
     * Convert OUT to IN
     *
     * @param outObject object ot convertIterableToList
     * @return converted object
     */
    fun convertOutToIn(outObject: OUT): IN


    fun convertOutToInRx(outObject: OUT): Flowable<IN?>


    /**
     * Convert List of IN to List of OUT
     *
     * @param inObjects [List] of objects to convertIterableToList
     * @return [List] of converted objects
     */
    fun convertListInToOut(inObjects: List<IN>?): List<OUT>?

    fun convertListInToOutRx(inObjects: List<IN>): Flowable<List<OUT>>?

    /**
     * Convert List of OUT to List of IN
     *
     * @param outObjects [List] of objects to convertIterableToList
     * @return [List] of converted objects
     */
    fun convertListOutToIn(outObjects: List<OUT>?): List<IN>?

    fun convertListOutToInRx(outObjects: List<OUT>): Flowable<List<IN>>?

    /**
     * Returns Transformer for converting Observable with single IN to OUT
     *
     * @return Instance of [FlowableTransformer]
     */
    fun singleINtoOUT(): FlowableTransformer<OUT, IN>

    /**
     * Returns Transformer for converting Observable with single OUT to IN
     *
     * @return Instance of [FlowableTransformer]
     */
    fun singleOUTtoIN(): FlowableTransformer<IN?, OUT>

    /**
     * Returns Transformer for converting Observable with List of IN to List of OUT
     *
     * @return Instance of [FlowableTransformer]
     */
    fun listINtoOUT(): FlowableTransformer<List<OUT>, List<IN>>

    /**
     * Returns Transformer for converting Observable with List of OUT to List of IN
     *
     * @return Instance of [FlowableTransformer]
     */
    fun listOUTtoIN(): FlowableTransformer<List<IN>, List<OUT>>

    fun singleINtoOUTSingle(): SingleTransformer<OUT, IN>

    fun singleOUTtoINSingle(): SingleTransformer<IN?, OUT>

    fun listINtoOUTSingle(): SingleTransformer<List<OUT>, List<IN>>

    fun listOUTtoINSingle(): SingleTransformer<List<IN>, List<OUT>>
}
