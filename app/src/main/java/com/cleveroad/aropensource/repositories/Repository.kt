package com.cleveroad.aropensource.repositories

import com.cleveroad.aropensource.models.Model
import io.reactivex.Single


interface Repository<M : Model<*>> {
    /**
     * Returns Flowable to subscribe for DataSetChanged event
     *
     * @return Instance of [<]
     */
    fun onDataSetChanged(): Single<Unit>

    /**
     * Returns Flowable to subscribe for datasetChanged events
     *
     * @return Instance of [<]
     */
    fun onItemChanged(): Single<M>

    /**
     * Returns Flowable to subscribe for ItemListChanged event
     *
     * @return Instance of [<]
     */
    fun onItemsListChanged(): Single<List<M>>

    /**
     * Returns Flowable to subscribe for ItemRemoved event
     *
     * @return Instance of [<]
     */
    fun onItemRemoved(): Single<M>

    /**
     * Returns Flowable to subscribe for ItemListRemoved event
     *
     * @return Instance of [<]
     */
    fun onItemsRemoved(): Single<List<M>>
}