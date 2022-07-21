package com.geekbrains.tests.model

import io.reactivex.Scheduler

internal interface SchedulersProvider {
    fun ui(): Scheduler
    fun io(): Scheduler
}