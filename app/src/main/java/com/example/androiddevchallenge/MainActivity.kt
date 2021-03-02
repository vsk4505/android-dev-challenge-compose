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

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.purple500
import com.example.androiddevchallenge.ui.theme.shapes
import com.example.androiddevchallenge.ui.theme.typography

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme() {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp(appViewModel: AppViewModel = viewModel()) {
    Surface(color = MaterialTheme.colors.background) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "ListScreen") {
            composable("ListScreen") {
                ListScreen(
                    appViewModel
                ) { navController.navigate("DetailsScreen/$it") }
            }
            composable("DetailsScreen/{index}") {
                DetailsScreen(
                    appViewModel,
                    it.arguments?.getString("index")?.toInt() ?: -1
                ) { navController.navigateUp() }
            }
        }
    }
}

@Composable
fun ListScreen(
    appViewModel: AppViewModel,
    onItemClick: (Int) -> Unit
) {
    val pets: Pets by appViewModel.pets.observeAsState(Pets(emptyList()))
    Column {
        TopAppBar(
            title = { Text(text = "Pets") },
            navigationIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_menu_24),
                    contentDescription = "Navigation Drawer",
                    Modifier.clickable { /* Handle Menu */ }
                )
            }
        )
        ListPets(pets, onItemClick)
    }
}

@Composable
fun DetailsScreen(
    appViewModel: AppViewModel,
    selectedItemIndex: Int,
    onBack: () -> Unit
) {
    val pets: Pets? by appViewModel.pets.observeAsState()
    val pet: Pet? = pets?.petsList?.get(selectedItemIndex)

    Column() {
        TopAppBar(
            title = { Text(text = pet?.name ?: "Pets") },
            navigationIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_arrow_back_24),
                    contentDescription = "Back Arrow",
                    Modifier.clickable { onBack() }
                )
            }
        )
        if (pet != null) {
            ShowPetDetails(pet)
        } else {
            ErrorView()
        }
    }
}

@Composable
fun ListPets(pets: Pets, onItemClick: (Int) -> Unit) {
    // We save the scrolling position with this state
    val scrollState = rememberLazyListState()

    LazyColumn(state = scrollState) {
        itemsIndexed(pets.petsList) { index, pet ->
            PetDisplayCard(pet) { onItemClick(index) }
        }
    }
}

@Composable
fun PetDisplayCard(pet: Pet, onCardClick: () -> Unit) {
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .clip(shape = shapes.medium)
            .padding(16.dp, 24.dp)
            .clickable(onClick = onCardClick)
    ) {
        PetBasicDetails(pet)
    }
}

@Composable
fun ShowPetDetails(pet: Pet) {
    // We save the scrolling position with this state
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        PetBasicDetails(pet)
        PetFullDetails(pet)
    }
}

@Composable
fun PetBasicDetails(pet: Pet) {
    Column {
        val drawableId = getResIdByName(LocalContext.current, pet.imageUrl)
        if (drawableId > 0) {
            Image(
                painter = painterResource(id = drawableId),
                contentDescription = "${pet.name}'s photo",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }
        Text(
            text = pet.name, style = typography.h5, textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 4.dp)
        )
    }
}

@Composable
fun PetFullDetails(pet: Pet) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp)) {
        Text(
            text = pet.description, style = typography.body1,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(0.dp, 4.dp))
        pet.details.let {
            Text(
                text = styleKeyValue("Type", it.type), style = typography.body1,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = styleKeyValue("Height", it.height), style = typography.body1,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = styleKeyValue("Weight", it.weight), style = typography.body1,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.padding(0.dp, 4.dp))
        Text(
            text = styleKeyValue("History", pet.history), style = typography.body1,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(0.dp, 4.dp))
        Text(
            text = styleKeyValue("Health"), style = typography.body1,
            modifier = Modifier.fillMaxWidth()
        )
        pet.health.forEach {
            Text(
                text = "\u2022 $it", style = typography.body1,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.padding(0.dp, 4.dp))
    }
}

@Composable
fun ErrorView() {
    Text(
        text = "Something went wrong!",
        style = typography.body1,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

private fun styleKeyValue(key: String, value: String = ""): AnnotatedString {
    return AnnotatedString.Builder(key)
        .apply {
            addStyle(SpanStyle(fontWeight = FontWeight.Bold, color = purple500), 0, length)
            if (value.isNotEmpty()) {
                append("\n$value")
            }
        }.toAnnotatedString()
}

@DrawableRes
fun getResIdByName(context: Context, name: String): Int {
    return context.resources.getIdentifier(name, "drawable", context.packageName)
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        val pet = Pet(
            id = "d3",
            name = "Pug",
            description = "Square-proportioned, compact and of a stocky build, the Pug is a large dog in a little space.",
            details = Details(type = "Toy", weight = "14-18 lb", height = "10-11\""),
            history = "The Pug has been known by many names: Mopshond in Holland (which refers to its grumbling tendencies);",
            health = listOf(
                "Major concerns: Pug dog encephalitis, CHD, brachycephalic syndrome",
                "Life span: 12–15 years"
            ),
            imageUrl = "d3_pug"
        )
        ShowPetDetails(pet = pet)
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        val pet = Pet(
            id = "d5",
            name = "Bernese Mountain Dog",
            description = "The Bernese Mountain Dog is slightly longer than tall, though appearing square.",
            details = Details(type = "Working", weight = "14-18 lb", height = "10-11\""),
            history = "The most well known of the Sennehunde, or Swiss mountain dogs",
            health = listOf(
                "Major concerns: Pug dog encephalitis, CHD, brachycephalic syndrome",
                "Life span: 12–15 years"
            ),
            imageUrl = "d5_bernese"
        )
        ShowPetDetails(pet = pet)
    }
}
