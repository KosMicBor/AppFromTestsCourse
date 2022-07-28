package com.geekbrains.tests.utils

import com.geekbrains.tests.model.SchedulersProvider
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class MainSchedulersProviderStub : SchedulersProvider {
    override fun ui(): Scheduler = Schedulers.trampoline()

    override fun io(): Scheduler = Schedulers.trampoline()
}