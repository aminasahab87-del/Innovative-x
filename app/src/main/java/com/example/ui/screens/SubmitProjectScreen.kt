package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.InnovationCategory
import com.example.ui.viewmodel.InnovateXViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitProjectScreen(
    viewModel: InnovateXViewModel,
    onSubmissionSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formState by viewModel.submissionForm.collectAsStateWithLifecycle()

    // Gallery Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()) {
            val newUrls = uris.map { it.toString() }
            val updated = (formState.photoUrls + newUrls).distinct()
            viewModel.updateFormStep3(updated, formState.videoUrl, formState.documentUrl)
        }
    }

    // Gallery Video Picker Launcher
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.updateFormStep3(formState.photoUrls, uri.toString(), formState.documentUrl)
        }
    }

    var showCategoryDropdown by remember { mutableStateOf(false) }
    var customPhotoUrlInput by remember { mutableStateOf("") }
    var showAddPhotoDialog by remember { mutableStateOf(false) }

    val presetPhotos = listOf(
        "https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1581092335397-9583fe92d232?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=800&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1532187863486-abf9dbad1b69?w=800&auto=format&fit=crop&q=80"
    )

    if (formState.isSubmittedSuccess) {
        // Success Completion View
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp)
                .testTag("submission_success_view"),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Submission Received!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "“Your innovation has been submitted successfully and is waiting for review.”",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Our reviewer panel will evaluate your prototype and provide feedback or approval notifications.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onSubmissionSuccess,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("success_continue_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("View My Projects", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("submit_project_screen")
    ) {
        // Top Step Progress Indicator
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (formState.isEditMode) "Edit Innovation" else "New Innovation Submission",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Step ${formState.currentStep} of 4",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Dots / Progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                (1..4).forEach { stepIndex ->
                    val isDone = stepIndex < formState.currentStep
                    val isCurrent = stepIndex == formState.currentStep
                    val color = when {
                        isDone -> Color(0xFF10B981)
                        isCurrent -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(color)
                    )
                }
            }
        }

        // Upload progress if submitting
        if (formState.isSubmitting) {
            LinearProgressIndicator(
                progress = { formState.uploadProgress },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Error Banner
        AnimatedVisibility(visible = formState.errorMessage != null) {
            formState.errorMessage?.let { errorMsg ->
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }
            }
        }

        // Form Step Content
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (formState.currentStep) {
                1 -> {
                    item {
                        Text(
                            text = "Step 1: Basic Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Tell us what your innovation is called and creator details.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = formState.title,
                            onValueChange = {
                                viewModel.updateFormStep1(
                                    title = it,
                                    category = formState.category,
                                    studentName = formState.studentName,
                                    school = formState.school,
                                    gradeClass = formState.gradeClass,
                                    cityState = formState.cityState
                                )
                            },
                            label = { Text("Project Name / Invention Title *") },
                            placeholder = { Text("e.g. Solar-Powered Hydroponics Tower") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_project_title"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    item {
                        ExposedDropdownMenuBox(
                            expanded = showCategoryDropdown,
                            onExpandedChange = { showCategoryDropdown = !showCategoryDropdown }
                        ) {
                            OutlinedTextField(
                                value = formState.category,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Innovation Category *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .testTag("dropdown_project_category"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = showCategoryDropdown,
                                onDismissRequest = { showCategoryDropdown = false }
                            ) {
                                InnovationCategory.allTitles().forEach { catTitle ->
                                    DropdownMenuItem(
                                        text = { Text(catTitle) },
                                        onClick = {
                                            viewModel.updateFormStep1(
                                                title = formState.title,
                                                category = catTitle,
                                                studentName = formState.studentName,
                                                school = formState.school,
                                                gradeClass = formState.gradeClass,
                                                cityState = formState.cityState
                                            )
                                            showCategoryDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = formState.studentName,
                            onValueChange = {
                                viewModel.updateFormStep1(
                                    title = formState.title,
                                    category = formState.category,
                                    studentName = it,
                                    school = formState.school,
                                    gradeClass = formState.gradeClass,
                                    cityState = formState.cityState
                                )
                            },
                            label = { Text("Student / Lead Creator Name *") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_student_name"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = formState.school,
                            onValueChange = {
                                viewModel.updateFormStep1(
                                    title = formState.title,
                                    category = formState.category,
                                    studentName = formState.studentName,
                                    school = it,
                                    gradeClass = formState.gradeClass,
                                    cityState = formState.cityState
                                )
                            },
                            label = { Text("School Name *") },
                            placeholder = { Text("e.g. St. Jude STEM Academy") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_school_name"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = formState.gradeClass,
                                onValueChange = {
                                    viewModel.updateFormStep1(
                                        title = formState.title,
                                        category = formState.category,
                                        studentName = formState.studentName,
                                        school = formState.school,
                                        gradeClass = it,
                                        cityState = formState.cityState
                                    )
                                },
                                label = { Text("Class / Grade") },
                                placeholder = { Text("Grade 11") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_grade_class"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = formState.cityState,
                                onValueChange = {
                                    viewModel.updateFormStep1(
                                        title = formState.title,
                                        category = formState.category,
                                        studentName = formState.studentName,
                                        school = formState.school,
                                        gradeClass = formState.gradeClass,
                                        cityState = it
                                    )
                                },
                                label = { Text("City, State") },
                                placeholder = { Text("San Jose, CA") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_city_state"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }

                2 -> {
                    item {
                        Text(
                            text = "Step 2: Technical Description & Solution",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Detail the scientific challenge and how your prototype functions.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = formState.problem,
                            onValueChange = {
                                viewModel.updateFormStep2(
                                    problem = it,
                                    solution = formState.solution,
                                    working = formState.working,
                                    innovation = formState.innovation,
                                    components = formState.components,
                                    estimatedCost = formState.estimatedCost
                                )
                            },
                            label = { Text("Problem Statement *") },
                            placeholder = { Text("What real-world problem does this solve?") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_problem_statement"),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 3
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = formState.solution,
                            onValueChange = {
                                viewModel.updateFormStep2(
                                    problem = formState.problem,
                                    solution = it,
                                    working = formState.working,
                                    innovation = formState.innovation,
                                    components = formState.components,
                                    estimatedCost = formState.estimatedCost
                                )
                            },
                            label = { Text("Proposed Solution *") },
                            placeholder = { Text("Describe your solution and core mechanism...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_proposed_solution"),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 3
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = formState.working,
                            onValueChange = {
                                viewModel.updateFormStep2(
                                    problem = formState.problem,
                                    solution = formState.solution,
                                    working = it,
                                    innovation = formState.innovation,
                                    components = formState.components,
                                    estimatedCost = formState.estimatedCost
                                )
                            },
                            label = { Text("How the Project Works *") },
                            placeholder = { Text("Step-by-step description of its operation...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_how_it_works"),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 3
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = formState.innovation,
                            onValueChange = {
                                viewModel.updateFormStep2(
                                    problem = formState.problem,
                                    solution = formState.solution,
                                    working = formState.working,
                                    innovation = it,
                                    components = formState.components,
                                    estimatedCost = formState.estimatedCost
                                )
                            },
                            label = { Text("What is Innovative About It?") },
                            placeholder = { Text("Why is this approach novel or better than existing tools?") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_what_is_innovative"),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 2
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = formState.components,
                            onValueChange = {
                                viewModel.updateFormStep2(
                                    problem = formState.problem,
                                    solution = formState.solution,
                                    working = formState.working,
                                    innovation = formState.innovation,
                                    components = it,
                                    estimatedCost = formState.estimatedCost
                                )
                            },
                            label = { Text("Components & Materials Used") },
                            placeholder = { Text("e.g. Arduino Nano, LiPo battery, ultrasonic sensor, 3D printed casing") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_components_materials"),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 2
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = formState.estimatedCost,
                            onValueChange = {
                                viewModel.updateFormStep2(
                                    problem = formState.problem,
                                    solution = formState.solution,
                                    working = formState.working,
                                    innovation = formState.innovation,
                                    components = formState.components,
                                    estimatedCost = it
                                )
                            },
                            label = { Text("Approximate Project Cost") },
                            placeholder = { Text("e.g. $45 USD") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_project_cost"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                3 -> {
                    item {
                        Text(
                            text = "Step 3: Media & Proof of Concept",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Upload photos of your build, a demo video link, and optional documentation.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Photos Section
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Project Photos (${formState.photoUrls.size})",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Row {
                                        IconButton(onClick = {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.AddPhotoAlternate,
                                                contentDescription = "Upload from Gallery",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        IconButton(onClick = { showAddPhotoDialog = !showAddPhotoDialog }) {
                                            Icon(
                                                imageVector = Icons.Default.Link,
                                                contentDescription = "Add Photo URL",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("upload_gallery_photos_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddPhotoAlternate,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Gallery se Picture Upload karein", fontSize = 12.sp)
                                    }
                                }

                                if (formState.photoUrls.isEmpty()) {
                                    Text(
                                        text = "Add photos to give reviewers a clear view of your prototype.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                } else {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.padding(top = 8.dp)
                                    ) {
                                        items(formState.photoUrls) { url ->
                                            Box(
                                                modifier = Modifier
                                                    .size(90.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                            ) {
                                                AsyncImage(
                                                    model = url,
                                                    contentDescription = null,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                                IconButton(
                                                    onClick = {
                                                        val updated = formState.photoUrls.filter { it != url }
                                                        viewModel.updateFormStep3(updated, formState.videoUrl, formState.documentUrl)
                                                    },
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .align(Alignment.TopEnd)
                                                        .background(Color.Black.copy(alpha = 0.6f))
                                                ) {
                                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(14.dp))
                                                }
                                            }
                                        }
                                    }
                                }

                                // Quick presets
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Or choose a high-resolution prototype photo preset:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(presetPhotos) { preset ->
                                        Surface(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable {
                                                    if (!formState.photoUrls.contains(preset)) {
                                                        viewModel.updateFormStep3(
                                                            formState.photoUrls + preset,
                                                            formState.videoUrl,
                                                            formState.documentUrl
                                                        )
                                                    }
                                                },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            AsyncImage(
                                                model = preset,
                                                contentDescription = "Preset",
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Video Section
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Demonstration Video",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Button(
                                    onClick = {
                                        videoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("upload_gallery_video_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Videocam,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Gallery se Video Pick karein (MP4 / MOV)")
                                }

                                if (formState.videoUrl.isNotBlank()) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = if (formState.videoUrl.startsWith("content://")) "Video Selected from Device Gallery" else formState.videoUrl,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Medium,
                                                    maxLines = 1
                                                )
                                            }
                                            IconButton(
                                                onClick = { viewModel.updateFormStep3(formState.photoUrls, "", formState.documentUrl) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Remove Video",
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Text(
                                    text = "Ya video link enter karein (YouTube / Google Drive / Direct URL):",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                OutlinedTextField(
                                    value = formState.videoUrl,
                                    onValueChange = {
                                        viewModel.updateFormStep3(
                                            formState.photoUrls,
                                            it,
                                            formState.documentUrl
                                        )
                                    },
                                    label = { Text("Video URL / Link") },
                                    placeholder = { Text("https://youtube.com/watch?v=... or Drive link") },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_video_url"),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }

                    // Document Section
                    item {
                        OutlinedTextField(
                            value = formState.documentUrl,
                            onValueChange = {
                                viewModel.updateFormStep3(
                                    formState.photoUrls,
                                    formState.videoUrl,
                                    it
                                )
                            },
                            label = { Text("Optional Project Document / PDF Title or Link") },
                            placeholder = { Text("e.g. Hydroponics_Yield_Report.pdf") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_document_url"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                4 -> {
                    item {
                        Text(
                            text = "Step 4: Review & Final Confirmation",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Please verify your submission details before sending to the review board.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = formState.title.ifBlank { "Untitled Innovation" },
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = formState.category,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Text(
                                    text = "Creator: ${formState.studentName} • ${formState.school} (${formState.gradeClass})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Problem Statement:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = formState.problem,
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Text(
                                    text = "Proposed Solution:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = formState.solution,
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Text(
                                    text = "How it Works:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = formState.working,
                                    style = MaterialTheme.typography.bodySmall
                                )

                                if (formState.components.isNotBlank()) {
                                    Text(
                                        text = "Components: ${formState.components}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (formState.estimatedCost.isNotBlank()) {
                                    Text(
                                        text = "Estimated Cost: ${formState.estimatedCost}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // AI Innovation Review Section
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                val previewAi = formState.aiPreviewResult

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "AI Innovation Review",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    if (previewAi != null) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "${previewAi.score}/100",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }

                                if (formState.isAiAnalyzing) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(22.dp),
                                            strokeWidth = 2.dp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Gemini AI project ko analyze kar raha hai...",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                } else if (previewAi != null) {
                                    val ai = previewAi

                                    // Verdict Chip
                                    Surface(
                                        color = if (ai.isGenuineInnovation) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = ai.verdict,
                                            color = if (ai.isGenuineInnovation) Color(0xFF15803D) else Color(0xFFB45309),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }

                                    // Innovation Analysis
                                    Text(
                                        text = "Analysis & Scientific Verdict:",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = ai.analysis,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    // What New to Add Section
                                    Text(
                                        text = "💡 Is Mein Kya Naya Add Karna Hai (Recommended Enhancements):",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    ai.whatNewToAdd.forEach { addition ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text("•", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                            Text(
                                                text = addition,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    OutlinedButton(
                                        onClick = { viewModel.runAiAnalysisOnDraft() },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Re-Analyze with AI", fontSize = 12.sp)
                                    }
                                } else {
                                    Text(
                                        text = "Upload karne se pehle AI se instant review lein ke aapka project real innovation hai ya is mein mazeed kya new features add kiye ja sakte hain!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Button(
                                        onClick = { viewModel.runAiAnalysisOnDraft() },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("run_ai_analysis_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Analyze Innovation with AI (Instant Feedback)")
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = formState.agreedToTerms,
                                    onCheckedChange = { viewModel.setSubmissionTermsAgreed(it) },
                                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.testTag("terms_checkbox")
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "I certify that this project is my original work (or school team work) and conforms to scientific ethics and laboratory safety guidelines.",
                                    style = MaterialTheme.typography.bodySmall,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.clickable {
                                        viewModel.setSubmissionTermsAgreed(!formState.agreedToTerms)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Navigation Buttons Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (formState.currentStep > 1) {
                    OutlinedButton(
                        onClick = { viewModel.goToPreviousStep() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("wizard_previous_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Back")
                    }
                } else {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (formState.currentStep == 4) {
                        OutlinedButton(
                            onClick = {
                                viewModel.submitProject(asDraft = true) { success, _ ->
                                    if (success) onSubmissionSuccess()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            enabled = !formState.isSubmitting,
                            modifier = Modifier.testTag("save_as_draft_button")
                        ) {
                            Text("Save Draft")
                        }

                        Button(
                            onClick = {
                                viewModel.submitProject(asDraft = false) { _, _ -> }
                            },
                            shape = RoundedCornerShape(12.dp),
                            enabled = !formState.isSubmitting && formState.agreedToTerms,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("submit_for_review_button")
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Submit for Review")
                        }
                    } else {
                        Button(
                            onClick = { viewModel.goToNextStep() },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("wizard_next_button")
                        ) {
                            Text("Next")
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
