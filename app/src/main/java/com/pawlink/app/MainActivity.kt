package com.pawlink.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val Brand = Color(0xFF5536D6)
private val BrandSoft = Color(0xFFEEEAFE)
private val AppBackground = Color(0xFFF6F4FA)
private val Ink = Color(0xFF171522)
private val Muted = Color(0xFF777386)
private val DogAccent = Color(0xFFF2A93B)
private val CatAccent = Color(0xFFEC6F91)

private enum class Screen { HOME, PETS, ADD_PET, VETS, SERVICES, PROFILE }
private enum class Species { DOG, CAT }

private data class Pet(
    val name: String,
    val species: Species,
    val breed: String,
    val dob: String,
    val weight: String,
    val owner: String,
    val email: String,
    val phone: String,
    val address: String,
    val problems: String,
    val medical: String
)

private data class Vet(val name: String, val specialty: String, val phone: String, val availability: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PawLinkTheme { PawLinkApp() } }
    }
}

@Composable
private fun PawLinkTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Brand,
            secondary = DogAccent,
            background = AppBackground,
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
    var splash by remember { mutableStateOf(true) }
    var screen by remember { mutableStateOf(Screen.HOME) }
    var filter by remember { mutableStateOf<Species?>(null) }
    val pets = remember {
        mutableStateListOf(
            Pet("Max", Species.DOG, "Golden Retriever", "2020-04-18", "31", "Richard", "", "", "", "", ""),
            Pet("Luna", Species.CAT, "British Shorthair", "2023-01-08", "4.8", "Richard", "", "", "", "", "")
        )
    }

    LaunchedEffect(Unit) {
        delay(1100)
        splash = false
    }

    if (splash) {
        Box(Modifier.fillMaxSize().background(Brand), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🐾", fontSize = 66.sp)
                Text("PawLink", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                Text("Health. Care. Connected.", color = Color.White.copy(alpha = .78f))
            }
        }
        return
    }

    Scaffold(
        containerColor = AppBackground,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavItem("⌂", "Home", screen == Screen.HOME) { screen = Screen.HOME }
                NavItem("🐾", "Pets", screen == Screen.PETS) { filter = null; screen = Screen.PETS }
                NavItem("🩺", "Vets", screen == Screen.VETS) { screen = Screen.VETS }
                NavItem("🏥", "Services", screen == Screen.SERVICES) { screen = Screen.SERVICES }
                NavItem("●", "Profile", screen == Screen.PROFILE) { screen = Screen.PROFILE }
            }
        },
        floatingActionButton = {
            if (screen != Screen.ADD_PET) {
                FloatingActionButton(onClick = { screen = Screen.ADD_PET }, containerColor = Brand, contentColor = Color.White) {
                    Text("+", fontSize = 28.sp)
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (screen) {
                Screen.HOME -> HomeScreen(
                    onDogs = { filter = Species.DOG; screen = Screen.PETS },
                    onCats = { filter = Species.CAT; screen = Screen.PETS },
                    onVets = { screen = Screen.VETS },
                    onServices = { screen = Screen.SERVICES },
                    onAdd = { screen = Screen.ADD_PET }
                )
                Screen.PETS -> PetsScreen(pets, filter)
                Screen.ADD_PET -> AddPetScreen(
                    onCancel = { screen = Screen.PETS },
                    onSave = { pet -> pets.add(pet); filter = pet.species; screen = Screen.PETS }
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
        icon = { Text(icon) },
        label = { Text(label, fontSize = 10.sp) },
        colors = NavigationBarItemDefaults.colors(indicatorColor = BrandSoft)
    )
}

@Composable
private fun HomeScreen(onDogs: () -> Unit, onCats: () -> Unit, onVets: () -> Unit, onServices: () -> Unit, onAdd: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        item {
            Text("PAWLINK", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("Your pets, one place.", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
        }
        item {
            Surface(shape = RoundedCornerShape(28.dp), color = Brand) {
                Column(Modifier.padding(22.dp)) {
                    Text("Health records and veterinary access", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Create pet profiles, keep essential medical information together, and reach veterinary professionals quickly.", color = Color.White.copy(alpha = .84f))
                    Spacer(Modifier.height(15.dp))
                    Button(onClick = onAdd, colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Brand)) {
                        Text("+ Add a pet", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item { SectionTitle("Choose a section") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SpeciesCard("🐶", "Dogs", DogAccent, Modifier.weight(1f), onDogs)
                SpeciesCard("🐱", "Cats", CatAccent, Modifier.weight(1f), onCats)
            }
        }
        item { SectionTitle("Quick access") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickCard("🩺", "Veterinarians", Modifier.weight(1f), onVets)
                QuickCard("🏥", "Facilities", Modifier.weight(1f), onServices)
            }
        }
        item { EmergencyCard() }
        item { Spacer(Modifier.height(65.dp)) }
    }
}

@Composable
private fun SpeciesCard(emoji: String, title: String, accent: Color, modifier: Modifier, onClick: () -> Unit) {
    Surface(modifier = modifier.clickable(onClick = onClick), shape = RoundedCornerShape(24.dp), color = Color.White, shadowElevation = 3.dp) {
        Column(Modifier.padding(18.dp)) {
            Text(emoji, fontSize = 40.sp)
            Spacer(Modifier.height(10.dp))
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Text("Profiles, health & care", color = Muted, fontSize = 12.sp)
            Spacer(Modifier.height(10.dp))
            Box(Modifier.fillMaxWidth().height(4.dp).background(accent, RoundedCornerShape(4.dp)))
        }
    }
}

@Composable
private fun QuickCard(emoji: String, title: String, modifier: Modifier, onClick: () -> Unit) {
    Surface(modifier = modifier.clickable(onClick = onClick), shape = RoundedCornerShape(20.dp), color = Color.White, shadowElevation = 2.dp) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 28.sp)
            Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
private fun EmergencyCard() {
    Surface(shape = RoundedCornerShape(22.dp), color = Color(0xFFFFECEF)) {
        Column(Modifier.padding(18.dp)) {
            Text("🚨 Emergency", fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFB8283B))
            Text("Emergency veterinary calling and location services will be connected in the production version.", color = Color(0xFF713C44), fontSize = 12.sp)
        }
    }
}

@Composable
private fun PetsScreen(pets: List<Pet>, filter: Species?) {
    val visible = if (filter == null) pets else pets.filter { it.species == filter }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text(when (filter) { Species.DOG -> "DOG SECTION"; Species.CAT -> "CAT SECTION"; null -> "MY PETS" }, color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(when (filter) { Species.DOG -> "Dogs"; Species.CAT -> "Cats"; null -> "All pets" }, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
        }
        if (visible.isEmpty()) item { InfoCard("No profiles yet", "Tap + to create the first pet profile.") }
        items(visible) { pet -> PetCard(pet) }
        item { SectionTitle("Health file") }
        item { InfoCard("Medical information", "Problems, medical issues, allergies, medication, operations and future uploaded reports.") }
        item { InfoCard("Preventive care", "Vaccination, medication and appointment reminders will be added to the persistent account version.") }
        item { Spacer(Modifier.height(65.dp)) }
    }
}

@Composable
private fun PetCard(pet: Pet) {
    Surface(shape = RoundedCornerShape(22.dp), color = Color.White, shadowElevation = 2.dp) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(if (pet.species == Species.DOG) "🐶" else "🐱", fontSize = 38.sp)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(pet.name, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    Text(listOf(pet.breed, pet.weight.takeIf { it.isNotBlank() }?.let { "$it kg" } ?: "").filter { it.isNotBlank() }.joinToString(" · "), color = Muted)
                }
            }
            if (pet.medical.isNotBlank() || pet.problems.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Text("Health notes", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(listOf(pet.problems, pet.medical).filter { it.isNotBlank() }.joinToString(" • "), color = Muted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun AddPetScreen(onCancel: () -> Unit, onSave: (Pet) -> Unit) {
    var name by remember { mutableStateOf("") }
    var species by remember { mutableStateOf(Species.DOG) }
    var breed by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var owner by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var problems by remember { mutableStateOf("") }
    var medical by remember { mutableStateOf("") }

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
        item {
            TextButton(onClick = onCancel) { Text("‹ Back") }
            Text("NEW PET", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("Create profile", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ChoiceButton("🐶 Dog", species == Species.DOG, Modifier.weight(1f)) { species = Species.DOG }
                ChoiceButton("🐱 Cat", species == Species.CAT, Modifier.weight(1f)) { species = Species.CAT }
            }
        }
        item { AppField("Pet name", name, { name = it }, "Max") }
        item { AppField("Breed", breed, { breed = it }, "Golden Retriever") }
        item { AppField("Date of birth", dob, { dob = it }, "YYYY-MM-DD") }
        item { AppField("Weight (kg)", weight, { weight = it }, "0.0", KeyboardType.Decimal) }
        item { SectionTitle("Owner") }
        item { AppField("Owner name", owner, { owner = it }, "Full name") }
        item { AppField("Email", email, { email = it }, "name@email.com", KeyboardType.Email) }
        item { AppField("Phone", phone, { phone = it }, "+961...", KeyboardType.Phone) }
        item { AppField("Address", address, { address = it }, "Address") }
        item { SectionTitle("Health") }
        item { AppField("Problems / concerns", problems, { problems = it }, "Symptoms or current concerns") }
        item { AppField("Medical issues", medical, { medical = it }, "Conditions, allergies, medications, operations") }
        item {
            Button(
                onClick = { if (name.isNotBlank()) onSave(Pet(name.trim(), species, breed.trim(), dob.trim(), weight.trim(), owner.trim(), email.trim(), phone.trim(), address.trim(), problems.trim(), medical.trim())) },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                enabled = name.isNotBlank()
            ) { Text("Save pet profile", fontWeight = FontWeight.Bold) }
        }
        item { Spacer(Modifier.height(65.dp)) }
    }
}

@Composable
private fun ChoiceButton(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Brand else Color.White,
            contentColor = if (selected) Color.White else Ink
        )
    ) { Text(text) }
}

@Composable
private fun AppField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String, keyboardType: KeyboardType = KeyboardType.Text) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun VetsScreen() {
    val context = LocalContext.current
    val vets = listOf(
        Vet("Dr. Maya Haddad", "Dogs & cats · General practice", "+96115550101", "Available today"),
        Vet("Dr. Karim Nassar", "Emergency & surgery", "+96115550102", "24/7 emergency"),
        Vet("Dr. Lea Mansour", "Feline medicine", "+96115550103", "Available tomorrow")
    )
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("VETERINARY DIRECTORY", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold); Text("Veterinarians", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold) }
        item { InfoCard("Prototype directory", "These profiles and numbers are placeholders. Production listings must be verified before public use.") }
        items(vets) { vet ->
            Surface(shape = RoundedCornerShape(22.dp), color = Color.White, shadowElevation = 2.dp) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("🩺 ${vet.name}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    Text(vet.specialty, color = Muted)
                    Text(vet.availability, color = Brand, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(Modifier.height(10.dp))
                    Button(onClick = {
                        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${vet.phone}")))
                    }) { Text("Call") }
                }
            }
        }
        item { Spacer(Modifier.height(65.dp)) }
    }
}

@Composable
private fun ServicesScreen() {
    val services = listOf(
        "🏥 Veterinary clinics & hospitals" to "Consultations, diagnostics and treatment",
        "🚨 Emergency care" to "Urgent and after-hours veterinary services",
        "🧪 Laboratories" to "Blood work and diagnostic testing",
        "✂️ Grooming" to "Dog and cat grooming services",
        "🛏️ Boarding" to "Pet boarding and supervised stays",
        "💊 Pet pharmacies" to "Veterinary and pet-care products",
        "🎓 Training" to "Behavior and dog-training services",
        "🚗 Pet transport" to "Transport to clinics and facilities"
    )
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
        item { Text("CARE NETWORK", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold); Text("Facilities", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold) }
        items(services) { (title, subtitle) -> InfoCard(title, subtitle) }
        item { Spacer(Modifier.height(65.dp)) }
    }
}

@Composable
private fun ProfileScreen() {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("OWNER ACCOUNT", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold); Text("Profile", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold) }
        item { InfoCard("Owner information", "Name, email, phone, address, emergency contact and preferred clinic will live here.") }
        item { InfoCard("Privacy & security", "Production storage will require secure authentication, encrypted transport and a proper privacy policy for owner and pet health information.") }
        item { InfoCard("PawLink 0.1.0", "Android prototype · temporary branding") }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
}

@Composable
private fun InfoCard(title: String, subtitle: String) {
    Surface(shape = RoundedCornerShape(18.dp), color = Color.White, shadowElevation = 1.dp) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(3.dp))
            Text(subtitle, color = Muted, fontSize = 12.sp)
        }
    }
}
