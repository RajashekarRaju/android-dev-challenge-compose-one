package com.example.androiddevchallenge.ui.cat

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.data.PuppyAdoptionRepo
import com.example.androiddevchallenge.model.Cats
import com.example.androiddevchallenge.ui.cats.getIconState
import com.example.androiddevchallenge.ui.theme.PuppyAdoptionTheme
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import dev.chrisbanes.accompanist.insets.statusBarsPadding


// Start of the Detail screen for each cat.
@ExperimentalAnimationApi
@Composable
fun CatDetails(
    catId: Int,
    navigateUp: () -> Unit
) {
    val context = LocalContext.current

    val cat: Cats = remember(catId) {
        PuppyAdoptionRepo.getCat(
            catId, context
        )
    }

    AnimatedVisibility(
        initiallyVisible = false,
        visible = true,
        enter = expandVertically(expandFrom = Alignment.Top, initialHeight = { 0 })
    ) {
        SetCatDetails(cat, navigateUp)
    }
}

// This UI starts with AnimatedVisibility
@ExperimentalAnimationApi
@Composable
private fun SetCatDetails(
    cat: Cats,
    navigateUp: () -> Unit
) {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
        ) {
            LazyColumn {
                item { CatHeader(cat.catImage, navigateUp) }
                item { CatInfoHeader(cat) }
                item { CatDetailBody(cat) }
                item { CatDetailAbout(cat) }
                item { CatAdoptButton(catName = cat.catName) }
            }
        }
    }
}

// Show up button, banner of the cat, custom app bar.
@Composable
private fun CatHeader(
    catImage: Int,
    navigateUp: () -> Unit
) {
    Box {
        Image(
            painter = painterResource(id = catImage),
            contentDescription = stringResource(R.string.content_desc_cat_detail_image),
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            contentScale = ContentScale.Crop
        )
        TopAppBar(
            backgroundColor = Color.Transparent,
            elevation = 0.dp,
            contentColor = Color.White,
            modifier = Modifier.statusBarsPadding()
        ) {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.content_desc_up_navigate_detail)
                )
            }
        }
    }
}

// Below banner image lazy row with 4 items show descriptive cat information.
@Composable
private fun CatInfoHeader(
    cat: Cats
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        item { HairColor(cat.catHairColor) }
        item { GenderInfo(cat.catGender) }
        item { EyeColor(cat.catEyeColor) }
        item { AgeInfo(cat.catAge) }
    }
}

// First item in lazy row, card with circle canvas and text in column
@Composable
private fun HairColor(
    catHairColor: Color
) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularCanvasCatInfoHeader(catHairColor)
        SubtitleForCatInfoHeader(stringResource(R.string.cat_hair_color_subtitle))
    }
}

// Second item in lazy row, card with circle canvas and text in column
@Composable
private fun GenderInfo(
    gender: String
) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            Modifier.clip(CircleShape),
            backgroundColor = MaterialTheme.colors.background
        ) {
            Image(
                painter = findCatGenderAndPaint(gender),
                contentDescription = stringResource(R.string.content_desc_cat_gender_picture),
                modifier = Modifier
                    .padding(16.dp)
                    .size(32.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
            )
        }
        SubtitleForCatInfoHeader(stringResource(R.string.cat_gender_subtitle))
    }
}

// Third item in lazy row, card with circle canvas and text in column
@Composable
private fun EyeColor(
    catEyeColor: Color
) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularCanvasCatInfoHeader(catEyeColor)
        SubtitleForCatInfoHeader(stringResource(R.string.cat_eye_color_subtitle))
    }
}

// Fourth item in lazy row, card with circle canvas and text in column
@Composable
private fun AgeInfo(
    catAge: Int
) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            Modifier.clip(CircleShape),
            backgroundColor = MaterialTheme.colors.background
        ) {
            Text(
                text = catAge.toString(),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )
        }
        SubtitleForCatInfoHeader(stringResource(R.string.cat_age_subtitle))
    }
}

// Horizontal divider
@Composable
private fun HeaderInfoDivider() {
    Divider(
        color = MaterialTheme.colors.secondary,
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

// Card with animated visibility.
// Initially shows title and subtitle with arrow button icon.
// Animates rest of the contents on clicking the button icon.
// Save state with remember and change current icon state.
// Card -> Column -> Row -> Boxes(weight 8.5f and 1.5f)
@ExperimentalAnimationApi
@Composable
private fun CatDetailBody(
    cat: Cats
) {
    var arrowExpanded by remember { mutableStateOf(false) }

    Card(
        shape = MaterialTheme.shapes.medium,
        backgroundColor = MaterialTheme.colors.background,
        modifier = Modifier
            .padding(8.dp)
            .animateContentSize()
    ) {
        Column {
            Row {

                Box(
                    Modifier.weight(8.5f)
                ) {
                    Column {
                        Text(
                            text = cat.catName,
                            modifier = Modifier.padding(top = 4.dp, start = 16.dp),
                            style = MaterialTheme.typography.h4,
                            color = MaterialTheme.colors.onSurface
                        )
                        Text(
                            text = cat.catDescription,
                            modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
                            style = MaterialTheme.typography.subtitle1,
                            color = MaterialTheme.colors.onSurface
                        )
                    }
                }

                Box(
                    Modifier
                        .weight(1.5f)
                        .padding(top = 12.dp),
                ) {
                    IconButton(onClick = { arrowExpanded = !arrowExpanded }) {
                        Icon(
                            painter = painterResource(getIconState(arrowExpanded)),
                            contentDescription = stringResource(id = R.string.content_desc_expand_cat_row_desc),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(arrowExpanded) {
                Column {
                    Row {
                        Image(
                            painter = painterResource(id = R.drawable.ic_cake),
                            contentDescription = stringResource(id = R.string.content_desc_expand_cat_row_desc),
                            modifier = Modifier
                                .padding(16.dp)
                                .size(40.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
                        )
                        Text(
                            text = cat.catDateOfBirth,
                            style = MaterialTheme.typography.h5,
                            modifier = Modifier.padding(vertical = 20.dp)
                        )
                    }
                }
            }
        }
    }
}

// Card with animated visibility.
// Initially shows image title and subtitle.
// Animates rest of the contents on clicking the button icon.
// Card -> Column -> Row -> Child's
@ExperimentalAnimationApi
@Composable
private fun CatDetailAbout(
    cat: Cats
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = MaterialTheme.shapes.medium,
        backgroundColor = MaterialTheme.colors.background,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(8.dp)
        ) {

            Row {
                Image(
                    painter = painterResource(id = R.drawable.ic_paw),
                    contentDescription = stringResource(R.string.content_desc_picture_paw_puppy),
                    modifier = Modifier
                        .padding(8.dp)
                        .size(32.dp),
                    colorFilter = ColorFilter.tint(cat.catEyeColor)
                )

                Text(
                    text = stringResource(R.string.cat_breed_header),
                    modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                    style = MaterialTheme.typography.h5,
                    color = MaterialTheme.colors.onSurface
                )
            }

            Text(
                text = cat.catBreed,
                modifier = Modifier.padding(top = 4.dp, start = 8.dp, bottom = 8.dp),
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface
            )

            AnimatedVisibility(expanded) {
                HeaderInfoDivider()
                Text(
                    text = cat.catAbout,
                    modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface,
                    textAlign = TextAlign.Justify
                )
            }
        }
    }
}

// Button for adopting the cat.
@Composable
private fun CatAdoptButton(catName: String) {

    val context = LocalContext.current

    Button(
        onClick = { Toast.makeText(context, catName, Toast.LENGTH_LONG).show() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(MaterialTheme.colors.secondary)
    ) {
        Text(
            text = stringResource(R.string.cat_adopt_button_name).plus(" ").plus(catName),
            style = MaterialTheme.typography.h6,
        )
    }
}

// Reusable composable for two lazy row items shows canvas
@Composable
private fun CircularCanvasCatInfoHeader(
    canvasColor: Color
) {
    Card(
        Modifier.clip(CircleShape),
        backgroundColor = MaterialTheme.colors.background
    ) {
        Canvas(
            modifier = Modifier
                .padding(16.dp)
                .size(32.dp),
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            drawCircle(
                color = canvasColor,
                center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
                radius = size.minDimension / 2
            )
        }
    }
}

// Reusable composable for all four lazy row items
@Composable
private fun SubtitleForCatInfoHeader(
    subtitle: String
) {
    Text(
        text = subtitle,
        style = MaterialTheme.typography.subtitle2,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

// Finds the gender of the cat using string and applies correct image resource.
@Composable
private fun findCatGenderAndPaint(
    gender: String
): Painter = if (gender == stringResource(id = R.string.cat_gender_male)) {
    painterResource(id = R.drawable.ic_male)
} else {
    painterResource(id = R.drawable.ic_female)
}

@ExperimentalAnimationApi
@Preview("Detail Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun DetailLightPreview() {
    PuppyAdoptionTheme {
        CatDetails(catId = 1, navigateUp = { })
    }
}

@ExperimentalAnimationApi
@Preview("Detail Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DetailDarkPreview() {
    PuppyAdoptionTheme(darkTheme = true) {
        CatDetails(catId = 1, navigateUp = { })
    }
}