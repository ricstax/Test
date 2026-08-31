package com.pawlink.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.Period

private val Brand = Color(0xFF5536D6)
private val BrandSoft = Color(0xFFEEEAFE)
private val Background = Color(0xFFF6F4FA)
private val Ink = Color(0xFF171522)
private val Muted = Color(0xFF777386)
private val DogAccent = Color(0xFFF2A93B)
private val CatAccent = Color(0xFFEC6F91)
private val Success = Color(0xFF2FB171)

private enum class Screen { HOME, PETS, ADD_PET, VETS, SERVICES, PROFILE }
private enum class Species { DOG, CAT }

data class Pet(
    val name: String,
    val species: Species,
    val breed: String,
    val dateOfBirth: String,
    val weightKg: String,
    val ownerName: String,
    val ownerEmail: String,
    val ownerPhone: String,
    val address: String,
    val problems: String,
    val medicalIssues: String
)

data class Vet(
    val name: String,
    val specialty: String,
    val availability: String,
    val emoji: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PawLinkTheme {
                PawLinkApp()
            }
        }
    }
}

@Composable
private fun PawLinkTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Brand,
            secondary = DogAccent,
            background = Background,
            surface = Color.White,
            onPrimary = Color.White,
            onBackground = Ink,
            onSurface = Ink
        ),
        content = content
    )
}

@Composable
private fun PawLinkApp() {
    var splashVisible by remember { mutableStateOf(true) }
    var screen by remember { mutableStateOf(Screen.HOME) }
    var petFilter by remember { mutableStateOf<Species?>(null) }

    val pets = remember {
        mutableStateListOf(
            Pet("Max", Species.DOG, "Golden Retriever", "2020-04-18", "31", "Richard", "", "", "", "", ""),
            Pet("Luna", Species.CAT, "British Shorthair", "2023-01-08", "4.8", "Richard", "", "", "", "", "")
        )
    }

    LaunchedEffect(Unit) {
        delay(1300)
        splashVisible = false
    }

    if (splashVisible) {
        SplashScreen()
        return
    }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavItem("⌂", "Home", screen == Screen.HOME) { screen = Screen.HOME }
                NavItem("🐾", "My Pets", screen == Screen.PETS) {
                    petFilter = null
                    screen = Screen.PETS
                }
                NavItem("🩺", "Vets", screen == Screen.VETS) { screen = Screen.VETS }
                NavItem("⌖", "Services", screen == Screen.SERVICES) { screen = Screen.SERVICES }
                NavItem("●", "Profile", screen == Screen.PROFILE) { screen = Screen.PROFILE }
            }
        },
        floatingActionButton = {
            if (screen != Screen.ADD_PET) {
                FloatingActionButton(
                    onClick = { screen = Screen.ADD_PET },
                    containerColor = Brand,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(18.dp)
                ) { Text("+", fontSize = 28.sp) }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (screen) {
                Screen.HOME -> HomeScreen(
                    onDogs = { petFilter = Species.DOG; screen = Screen.PETS },
                    onCats = { petFilter = Species.CAT; screen = Screen.PETS },
                    onAddPet = { screen = Screen.ADD_PET },
                    onVets = { screen = Screen.VETS },
                    onServices = { screen = Screen.SERVICES }
                )
                Screen.PETS -> PetsScreen(pets, petFilter) { pets.remove(it) }
                Screen.ADD_PET -> AddPetScreen(
                    onCancel = { screen = Screen.PETS },
                    onSave = {
                        pets.add(it)
                        petFilter = it.species
                        screen = Screen.PETS
                    }
                )
                Screen.VETS -> VetsScreen()
                Screen.SERVICES -> ServicesScreen()
                Screen.PROFILE -> ProfileScreen()
            }
        }
    }
}

@Composable
private fun RowScope.NavItem(icon: String, label: String, selected: Boolean, onClick: () -> Unit) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { Text(icon, fontSize = 20.sp) },
        label = { Text(label, fontSize = 10.sp) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Brand,
            selectedTextColor = Brand,
            indicatorColor = BrandSoft,
            unselectedIconColor = Muted,
            unselectedTextColor = Muted
        )
    )
}

@Composable
private fun SplashScreen() {
    Box(
        Modifier.fillMaxSize().background(Brand),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(94.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) { Text("🐾", fontSize = 47.sp) }
            }
            Spacer(Modifier.height(18.dp))
            Text("PawLink", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
            Text("Health. Care. Connected.", color = Color.White.copy(alpha = .75f), fontSize = 15.sp)
        }
    }
}

@Composable
private fun HomeScreen(
    onDogs: () -> Unit,
    onCats: () -> Unit,
    onAddPet: () -> Unit,
    onVets: () -> Unit,
    onServices: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("GOOD EVENING", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Hi, Richard", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                }
                CircleBubble("👤")
            }
        }
        item {
            Surface(shape = RoundedCornerShape(30.dp), color = Brand, tonalElevation = 2.dp) {
                Column(Modifier.padding(24.dp)) {
                    Text("Your pets, one place.", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Keep health records organized and reach the right veterinary professional when you need them.",
                        color = Color.White.copy(alpha = .82f),
                        lineHeight = 21.sp
                    )
                    Spacer(Modifier.height(18.dp))
                    Button(
                        onClick = onAddPet,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Brand)
                    ) { Text("+ Add a pet", fontWeight = FontWeight.Bold) }
                }
            }
        }
        item { SectionTitle("Choose a section") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SpeciesCard("🐶", "Dogs", "Profiles, health & care", DogAccent, Modifier.weight(1f), onDogs)
                SpeciesCard("🐱", "Cats", "Profiles, health & care", CatAccent, Modifier.weight(1f), onCats)
            }
        }
        item { SectionTitle("Quick access") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickCard("🩺", "Vets", Modifier.weight(1f), onVets)
                QuickCard("🏥", "Facilities", Modifier.weight(1f), onServices)
                QuickCard("🚨", "Emergency", Modifier.weight(1f)) { }
            }
        }
        item { Spacer(Modifier.height(70.dp)) }
    }
}

@Composable
private fun SpeciesCard(
    emoji: String,
    title: String,
    subtitle: String,
    accent: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(25.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(emoji, fontSize = 42.sp)
            Spacer(Modifier.height(12.dp))
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Text(subtitle, color = Muted, fontSize = 12.sp)
            Spacer(Modifier.height(10.dp))
            Box(Modifier.fillMaxWidth().height(4.dp).background(accent, RoundedCornerShape(4.dp)))
        }
    }
}

@Composable
private fun QuickCard(emoji: String, title: String, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            Modifier.padding(vertical = 16.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 25.sp)
            Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
private fun PetsScreen(pets: List<Pet>, filter: Species?, onDelete: (Pet) -> Unit) {
    val filtered = if (filter == null) pets else pets.filter { it.species == filter }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Header(
                eyebrow = when (filter) { Species.DOG -> "DOG SECTION"; Species.CAT -> "CAT SECTION"; null -> "MY PETS" },
                title = when (filter) { Species.DOG -> "Dogs"; Species.CAT -> "Cats"; null -> "All pets" },
                emoji = "🐾"
            )
        }
        if (filtered.isEmpty()) {
            item { EmptyState("No pet profiles here yet.") }
        } else {
            items(filtered) { pet -> PetCard(pet, onDelete) }
        }
        item { SectionTitle("Health overview") }
        item { InfoCard("Vaccination reminders", "Track upcoming vaccinations and preventative care.") }
        item { InfoCard("Medical records", "Conditions, allergies, medication, operations, tests and uploaded documents.") }
        item { InfoCard("Appointments", "Keep upcoming consultations and follow-up visits in one place.") }
        item { Spacer(Modifier.height(70.dp)) }
    }
}

@Composable
private fun PetCard(pet: Pet, onDelete: (Pet) -> Unit) {
    Surface(shape = RoundedCornerShape(23.dp), color = Color.White, shadowElevation = 2.dp) {
        Row(Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(18.dp), color = BrandSoft) {
                Box(Modifier.size(62.dp), contentAlignment = Alignment.Center) {
                    Text(if (pet.species == Species.DOG) "🐶" else "🐱", fontSize = 31.sp)
                }
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Text(pet.name, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Text(
                    listOfNotNull(
                        pet.breed.takeIf { it.isNotBlank() },
                        ageLabel(pet.dateOfBirth),
                        pet.weightKg.takeIf { it.isNotBlank() }?.let { "$it kg" }
                    ).joinToString(" · "),
                    color = Muted,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(6.dp))
                Surface(color = BrandSoft, shape = RoundedCornerShape(50)) {
                    Text(
                        if (pet.species == Species.DOG) "Dog profile" else "Cat profile",
                        color = Brand,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            TextButton(onClick = { onDelete(pet) }) { Text("Remove", color = Color(0xFFCA4050), fontSize = 11.sp) }
        }
    }
}

private fun ageLabel(date: String): String? {
    if (date.isBlank()) return null
    return try {
        val years = Period.between(LocalDate.parse(date), LocalDate.now()).years
        "$years yr${if (years == 1) "" else "s"}"
    } catch (_: Exception) { null }
}

@Composable
private fun AddPetScreen(onCancel: () -> Unit, onSave: (Pet) -> Unit) {
    var petName by remember { mutableStateOf("") }
    var species by remember { mutableStateOf(Species.DOG) }
    var breed by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("Richard") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var problems by remember { mutableStateOf("") }
    var medicalIssues by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onCancel) { Text("‹ Back", color = Brand, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text("NEW PROFILE", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Add a pet", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
        item {
            Notice("This first native version keeps data in memory. Production will add secure accounts, persistent storage and verified veterinary listings.")
        }
        item { AppField("Pet name", petName, { petName = it }, "e.g. Max") }
        item {
            Column {
                FieldLabel("Type")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ChoiceButton("🐶 Dog", species == Species.DOG, Modifier.weight(1f)) { species = Species.DOG }
                    ChoiceButton("🐱 Cat", species == Species.CAT, Modifier.weight(1f)) { species = Species.CAT }
                }
            }
        }
        item { AppField("Breed", breed, { breed = it }, "Breed") }
        item { AppField("Date of birth", dob, { dob = it }, "YYYY-MM-DD") }
        item { AppField("Weight (kg)", weight, { weight = it }, "0.0", KeyboardType.Decimal) }
        item { AppField("Owner name", ownerName, { ownerName = it }, "Full name") }
        item { AppField("Owner email", email, { email = it }, "name@email.com", KeyboardType.Email) }
        item { AppField("Owner phone", phone, { phone = it }, "+961 ...", KeyboardType.Phone) }
        item { AppField("Address", address, { address = it }, "City / address") }
        item { AppField("Problems / concerns", problems, { problems = it }, "Behavior, diet, recurring concerns...", multiline = true) }
        item { AppField("Medical issues", medicalIssues, { medicalIssues = it }, "Conditions, allergies, medications, previous operations...", multiline = true) }
        item {
            Button(
                onClick = {
                    if (petName.isNotBlank()) {
                        onSave(Pet(petName, species, breed, dob, weight, ownerName, email, phone, address, problems, medicalIssues))
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Brand)
            ) { Text("SAVE PET PROFILE", fontWeight = FontWeight.ExtraBold) }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun AppField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    multiline: Boolean = false
) {
    Column {
        FieldLabel(label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Muted.copy(alpha = .65f)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            minLines = if (multiline) 3 else 1,
            maxLines = if (multiline) 5 else 1,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Brand,
                unfocusedBorderColor = Color(0xFFE4E0EB),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text.uppercase(), color = Muted, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(start = 3.dp, bottom = 6.dp))
}

@Composable
private fun ChoiceButton(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Brand else Color.White,
            contentColor = if (selected) Color.White else Ink
        ),
        shape = RoundedCornerShape(14.dp)
    ) { Text(text, fontWeight = FontWeight.Bold) }
}

@Composable
private fun VetsScreen() {
    val vets = listOf(
        Vet("Dr. Maya Haddad", "Small animal medicine · Dogs & Cats", "Available today", "👩‍⚕️"),
        Vet("Dr. Karim Nader", "Emergency & internal medicine", "24/7 clinic", "👨‍⚕️"),
        Vet("Dr. Lea Farah", "Feline medicine · Cats", "Cat specialist", "👩‍⚕️"),
        Vet("Dr. Sami Khoury", "Surgery · Dogs & Cats", "Appointments", "👨‍⚕️")
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Header("CARE NETWORK", "Veterinarians", "🩺") }
        item { Notice("Demo veterinarian profiles are fictional placeholders. Production listings should be verified before publishing or enabling calls.") }
        items(vets) { vet -> VetCard(vet) }
        item { Spacer(Modifier.height(70.dp)) }
    }
}

@Composable
private fun VetCard(vet: Vet) {
    Surface(shape = RoundedCornerShape(23.dp), color = Color.White, shadowElevation = 2.dp) {
        Row(Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(18.dp), color = BrandSoft) {
                Box(Modifier.size(61.dp), contentAlignment = Alignment.Center) { Text(vet.emoji, fontSize = 30.sp) }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(vet.name, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Text(vet.specialty, color = Muted, fontSize = 12.sp)
                Spacer(Modifier.height(5.dp))
                Text(vet.availability, color = Brand, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            FilledTonalButton(
                onClick = { },
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFFE6F8EF), contentColor = Success),
                contentPadding = PaddingValues(horizontal = 13.dp)
            ) { Text("☎", fontSize = 18.sp) }
        }
    }
}

@Composable
private fun ServicesScreen() {
    val services = listOf(
        Triple("🏥", "Veterinary clinics", "Consultations, diagnostics and treatment."),
        Triple("🧪", "Laboratories", "Tests and diagnostic services."),
        Triple("✂️", "Grooming", "Dog and cat grooming facilities."),
        Triple("🛏️", "Boarding", "Pet hotels and supervised stays."),
        Triple("💊", "Pet pharmacies", "Veterinary medicines and care products."),
        Triple("🎓", "Training", "Behavior and training professionals."),
        Triple("🚐", "Pet transport", "Transport services for pets and emergencies.")
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Header("CARE DIRECTORY", "Facilities", "🏥") }
        items(services) { (emoji, name, description) -> ServiceCard(emoji, name, description) }
        item { Spacer(Modifier.height(70.dp)) }
    }
}

@Composable
private fun ServiceCard(emoji: String, name: String, description: String) {
    Surface(shape = RoundedCornerShape(23.dp), color = Color.White, shadowElevation = 2.dp) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(18.dp), color = BrandSoft) {
                Box(Modifier.size(60.dp), contentAlignment = Alignment.Center) { Text(emoji, fontSize = 28.sp) }
            }
            Spacer(Modifier.width(13.dp))
            Column {
                Text(name, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Text(description, color = Muted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ProfileScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Column(Modifier.fillMaxWidth().padding(vertical = 14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(shape = RoundedCornerShape(27.dp), color = BrandSoft) {
                    Box(Modifier.size(86.dp), contentAlignment = Alignment.Center) { Text("👤", fontSize = 41.sp) }
                }
                Spacer(Modifier.height(10.dp))
                Text("Richard", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                Text("Pet owner profile", color = Muted)
            }
        }
        item { SettingRow("Personal information") }
        item { SettingRow("Emergency contact") }
        item { SettingRow("Preferred veterinarian") }
        item { SettingRow("Notifications") }
        item { SettingRow("Privacy & data") }
        item { Spacer(Modifier.height(70.dp)) }
    }
}

@Composable
private fun SettingRow(label: String) {
    Surface(shape = RoundedCornerShape(18.dp), color = Color.White) {
        Row(Modifier.fillMaxWidth().padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
            Text("›", color = Muted, fontSize = 22.sp)
        }
    }
}

@Composable
private fun Header(eyebrow: String, title: String, emoji: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(eyebrow, color = Muted, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
            Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 29.sp)
        }
        CircleBubble(emoji)
    }
}

@Composable
private fun CircleBubble(emoji: String) {
    Surface(shape = CircleShape, color = BrandSoft) {
        Box(Modifier.size(47.dp), contentAlignment = Alignment.Center) { Text(emoji, fontSize = 21.sp) }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
}

@Composable
private fun InfoCard(title: String, description: String) {
    Surface(shape = RoundedCornerShape(21.dp), color = Color.White, shadowElevation = 1.dp) {
        Column(Modifier.fillMaxWidth().padding(17.dp)) {
            Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            Text(description, color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
        }
    }
}

@Composable
private fun Notice(text: String) {
    Surface(shape = RoundedCornerShape(15.dp), color = BrandSoft) {
        Text(text, color = Muted, fontSize = 11.sp, lineHeight = 17.sp, modifier = Modifier.padding(13.dp))
    }
}

@Composable
private fun EmptyState(text: String) {
    Text(text, modifier = Modifier.fillMaxWidth().padding(34.dp), color = Muted, textAlign = TextAlign.Center)
}
