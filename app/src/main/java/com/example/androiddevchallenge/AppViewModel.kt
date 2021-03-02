/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import java.io.InputStreamReader

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val _pets: MutableLiveData<Pets> = MutableLiveData<Pets>()
    val pets: LiveData<Pets> = _pets

    init {
        val gson = Gson()
        val reader = InputStreamReader(application.resources.openRawResource(R.raw.pets_list))
        _pets.value = gson.fromJson(reader, Pets::class.java)
    }
}
