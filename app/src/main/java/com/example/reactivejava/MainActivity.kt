package com.example.reactivejava

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/*
DataSet -> Observable
we are attaching a Observer to the Observable
and manipulating data in between and observe there changes
All the events of data coming by any means and we observing and react to it is the reactive java
eg - click handlers, network calls,
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.tv_hello)
        simpleObserver()
        createObservable()

        /*
        this is to throttle first then clicking using rxJava
         */
        textView.clicks()
            .throttleFirst(1500, TimeUnit.MILLISECONDS)
            .subscribe {
                Log.d("taget", "Clicked")
            }
        implementNetworkCall()
    }

    private fun implementNetworkCall() {
        val retrofit = Retrofit.Builder().baseUrl("https://fakestoreapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create()).build()

        val productService = retrofit.create(ProductService::class.java)
        /*
        here we are calling the API, here we ase subscribeOn() on io thread as it is working upstream(up code)
        and we are using observeOn() on main thread as that is working downstream(down code)
        and subscribe is basically to call onNext for observer
         */
        productService.getProducts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.d("taget", it.toString())
            }

    }

    //here we are creating observers
    private fun createObservable() {
        val observable = Observable.create<String> {
            it.onNext("One")
            it.onNext("Two")
            it.onComplete()
        }
        observable.subscribe(object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                Log.d("taget", "1")
            }

            override fun onNext(t: String) {
                Log.d("taget", "2")
            }

            override fun onError(e: Throwable) {
                Log.d("taget", "3")
            }

            override fun onComplete() {
                Log.d("taget", "4")
            }
        })
    }

    private fun simpleObserver() {
        val list = listOf<String>("A", "B", "C")
        val observable = Observable.fromIterable(list)
        observable.subscribe(object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                Log.d("taget", "1")
            }

            override fun onNext(t: String) {
                Log.d("taget", "2")
            }

            override fun onError(e: Throwable) {
                Log.d("taget", "3")
            }

            override fun onComplete() {
                Log.d("taget", "4")
            }
        })
    }
}