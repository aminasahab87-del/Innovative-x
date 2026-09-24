package com.example.data.local

import com.example.data.model.InnovationBadge
import com.example.data.model.InnovationCategory
import com.example.data.model.LiveClassSession
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.model.Project
import com.example.data.model.ProjectStatus
import com.example.data.model.Review
import com.example.data.model.User
import com.example.data.model.UserRole

object InitialData {
    val sampleUsers = listOf(
        User(
            id = "user_dev_aafaq",
            email = "aafaq.hussain@innovatex.edu",
            passwordHash = "aafaq123",
            name = "Aafaq Hussain",
            role = UserRole.ADMIN.roleKey,
            school = "InnovateX Lead Engineering & Development",
            gradeClass = "Chief Software Architect & Lead Developer",
            city = "Silicon Valley",
            state = "California",
            bio = "Lead Developer & System Architect of InnovateX STEM Platform. Empowering student scientists and young engineers worldwide.",
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80",
            isPublicInfoVisible = true,
            createdAt = System.currentTimeMillis() - 86400000L * 90
        ),
        User(
            id = "user_student_1",
            email = "student@innovatex.edu",
            passwordHash = "password123",
            name = "Alex Rivera",
            role = UserRole.STUDENT.roleKey,
            school = "St. Jude STEM Academy",
            gradeClass = "Grade 11 - Robotics & Physics",
            city = "San Jose",
            state = "California",
            bio = "High school inventor dedicated to smart agriculture, clean energy, and accessible assistive hardware.",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
            isPublicInfoVisible = true,
            createdAt = System.currentTimeMillis() - 86400000L * 30
        ),
        User(
            id = "user_admin_1",
            email = "admin@innovatex.edu",
            passwordHash = "admin123",
            name = "Dr. Elena Vance",
            role = UserRole.ADMIN.roleKey,
            school = "Pacific Science Institute & Youth Innovation Board",
            gradeClass = "Chief Reviewer",
            city = "Palo Alto",
            state = "California",
            bio = "Chair of Student STEM Innovations. Reviewing youth scientific prototypes, patent disclosures, and national showcase finalists.",
            avatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=400&auto=format&fit=crop&q=80",
            isPublicInfoVisible = true,
            createdAt = System.currentTimeMillis() - 86400000L * 60
        ),
        User(
            id = "user_student_2",
            email = "maya.chen@innovatex.edu",
            passwordHash = "password123",
            name = "Maya Chen",
            role = UserRole.STUDENT.roleKey,
            school = "Bay Area Tech High",
            gradeClass = "Grade 12 - Biotechnology",
            city = "Oakland",
            state = "California",
            bio = "Passionate about bioengineering, low-cost prosthetics, and autonomous environmental sensors.",
            avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400&auto=format&fit=crop&q=80",
            isPublicInfoVisible = true,
            createdAt = System.currentTimeMillis() - 86400000L * 45
        )
    )

    val sampleProjects = listOf(
        Project(
            id = "proj_arduino_obstacle_robot",
            ownerId = "user_student_1",
            ownerName = "Alex Rivera",
            school = "St. Jude STEM Academy",
            gradeClass = "Grade 11 - Robotics & Embedded Systems",
            cityState = "San Jose, CA",
            title = "Arduino Uno Autonomous Obstacle-Avoiding & Maze Rover",
            category = InnovationCategory.ROBOTICS.title,
            problem = "Emergency first-responders need lightweight, low-cost autonomous robots that can scout obstacle-filled corridors and hazardous areas without continuous radio control.",
            solution = "An Arduino Uno R3 rover integrating an ultrasonic HC-SR04 distance sensor on an SG90 servo turret and an L298N dual motor driver to map and navigate around obstacles in real-time.",
            working = "The ATmega328P runs an interrupt-driven look-ahead algorithm. When distance drops below 25cm, the rover halts, pans left and right 60 degrees, evaluates clear pathway angles, and executes precision PWM turns.",
            innovation = "Features dual power rail isolation to prevent motor inductive kickback from resetting the Arduino Uno, plus a 16x2 I2C telemetry readout and warning buzzer system.",
            components = "Arduino Uno R3 (ATmega328P), HC-SR04 Ultrasonic Distance Sensor, SG90 Micro Servo, L298N Dual H-Bridge Motor Driver, 2x TT Gear DC Motors, 16x2 I2C LCD, 2x 18650 Li-ion Batteries, Acrylic Chassis.",
            estimatedCost = "$26 USD",
            photoUrls = listOf(
                "https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=800&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=800&auto=format&fit=crop&q=80"
            ),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            documentUrl = "Arduino_Uno_Obstacle_Rover_Schematic.pdf",
            status = ProjectStatus.PUBLISHED.name,
            isFeatured = true,
            reviewerId = "user_admin_1",
            reviewerName = "Dr. Elena Vance",
            reviewerFeedback = "Outstanding embedded C++ architecture on Arduino Uno! Responsive sensor feedback loop and clean wiring diagram verified.",
            badgeAwarded = InnovationBadge.TECHNOLOGY_INNOVATION.title,
            aiInnovationScore = 88,
            aiVerdict = "🌟 High Innovation Potential (Responsive Edge Rover)",
            aiAnalysis = "Solid implementation of autonomous obstacle traversal on Arduino Uno. The dual-rail power isolation shows great engineering discipline by eliminating back-EMF processor resets.",
            aiSuggestions = "• Add an HC-05 Bluetooth or ESP32 module to stream 2D radar obstacle heatmaps to an Android companion app.\n• Integrate optical wheel encoders for closed-loop odometry and dead-reckoning navigation.\n• Add an emergency soft bumper microswitch as an ultra-reliable mechanical fail-safe.",
            viewsCount = 1890,
            likesCount = 275,
            createdAt = System.currentTimeMillis() - 86400000L * 7,
            updatedAt = System.currentTimeMillis() - 86400000L * 1,
            publishedAt = System.currentTimeMillis() - 86400000L * 1
        ),
        Project(
            id = "proj_hydroponics",
            ownerId = "user_student_1",
            ownerName = "Alex Rivera",
            school = "St. Jude STEM Academy",
            gradeClass = "Grade 11",
            cityState = "San Jose, CA",
            title = "Solar-Powered Automated Hydroponics Farm",
            category = InnovationCategory.AGRICULTURE.title,
            problem = "Conventional urban community gardens waste up to 70% of irrigation water through soil runoff and lack 24/7 nutrient monitoring, making fresh produce difficult to grow sustainably in arid areas.",
            solution = "A closed-loop vertical hydroponic tower powered by a 50W solar panel and battery backup that automates pH adjustment, nutrient dosing, and LED spectrum cycles via an ESP32 microcontroller.",
            working = "Water mixed with organic nutrients is pumped upwards every 15 minutes through PVC columns with Rockwool pods. Dual analog sensors measure electrical conductivity (EC) and pH. If pH drifts from 5.8-6.2, mini peristaltic pumps inject organic buffer solutions.",
            innovation = "Integrates zero-grid solar energy harvesting with custom 3D-printed modular nutrient flow distributors that consume 92% less water than soil agriculture with zero electrical grid footprint.",
            components = "ESP32 DevKit, 50W Monocrystalline Solar Panel, 12V LiFePO4 battery, 12V submersible brushless water pump, gravity pH sensor probe, TDS sensor, 3D-printed PLA crop towers, Full-spectrum grow LEDs.",
            estimatedCost = "$115 USD",
            photoUrls = listOf(
                "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=800&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1530836369250-ef72a3f5cda8?w=800&auto=format&fit=crop&q=80"
            ),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            documentUrl = "Hydroponics_Schematics_and_Yield_Report.pdf",
            status = ProjectStatus.PUBLISHED.name,
            isFeatured = true,
            reviewerId = "user_admin_1",
            reviewerName = "Dr. Elena Vance",
            reviewerFeedback = "Exceptional engineering rigour! The closed-loop irrigation system and power budget were verified successfully with healthy kale and lettuce yields over 4 weeks.",
            badgeAwarded = InnovationBadge.GREEN_INNOVATION.title,
            aiInnovationScore = 93,
            aiVerdict = "🌟 Breakthrough Innovation (Zero-Grid AgriTech)",
            aiAnalysis = "Remarkable closed-loop hydroponic architecture powered entirely by solar energy. Automated peristaltic dosing ensures stable pH/EC levels for maximum plant vitality without water runoff.",
            aiSuggestions = "• Add an ESP32-CAM module running TinyML to visually detect early fungal spots or nitrogen deficiencies on leaves.\n• Integrate an MQTT cloud dashboard to record daily water consumption and nutrient consumption metrics.\n• Implement a battery-backed passive siphoning failsafe for prolonged overcast days.",
            viewsCount = 1240,
            likesCount = 186,
            createdAt = System.currentTimeMillis() - 86400000L * 14,
            updatedAt = System.currentTimeMillis() - 86400000L * 5,
            publishedAt = System.currentTimeMillis() - 86400000L * 5
        ),
        Project(
            id = "proj_sensory_vest",
            ownerId = "user_student_1",
            ownerName = "Alex Rivera",
            school = "St. Jude STEM Academy",
            gradeClass = "Grade 11",
            cityState = "San Jose, CA",
            title = "AI Blind Navigation Sensory Vest",
            category = InnovationCategory.ARTIFICIAL_INTELLIGENCE.title,
            problem = "Visually impaired individuals face dangerous obstacles at head and chest height that traditional white canes fail to detect, such as open truck tailgates and hanging branches.",
            solution = "A lightweight wearable vest equipped with dual wide-angle stereoscopic depth cameras and edge neural networks that translates spatial obstacles into directional haptic vibrations on the wearer's torso.",
            working = "Images are captured at 30fps and fed to an on-device quantized MobileNet model running on a Raspberry Pi Zero 2W. Objects within 2.5 meters trigger an 8-point ERM vibration motor matrix corresponding to clock directions.",
            innovation = "Avoids auditory sensory overload by communicating exclusively through subtle haptic directional cues, enabling users to hear natural environmental cues and conversations unimpeded.",
            components = "Raspberry Pi Zero 2W, Dual Wide-Angle USB Camera Module, PCA9685 I2C PWM driver, 8x Mini disc vibration motors, Neoprene lightweight running harness, 5V 10000mAh Power Bank.",
            estimatedCost = "$88 USD",
            photoUrls = listOf(
                "https://images.unsplash.com/photo-1507413245164-6160d8298b31?w=800&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=800&auto=format&fit=crop&q=80"
            ),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            documentUrl = "Sensory_Vest_Latency_Tests.pdf",
            status = ProjectStatus.PUBLISHED.name,
            isFeatured = true,
            reviewerId = "user_admin_1",
            reviewerName = "Dr. Elena Vance",
            reviewerFeedback = "Remarkable safety-first approach. Blind community user testing data was well-documented with 94% obstacle evasion rate during school hallway trials.",
            badgeAwarded = InnovationBadge.TECHNOLOGY_INNOVATION.title,
            viewsCount = 945,
            likesCount = 142,
            createdAt = System.currentTimeMillis() - 86400000L * 20,
            updatedAt = System.currentTimeMillis() - 86400000L * 8,
            publishedAt = System.currentTimeMillis() - 86400000L * 8
        ),
        Project(
            id = "proj_water_filtration",
            ownerId = "user_student_1",
            ownerName = "Alex Rivera",
            school = "St. Jude STEM Academy",
            gradeClass = "Grade 11",
            cityState = "San Jose, CA",
            title = "Low-Cost Microplastic Water Filtration Sensor",
            category = InnovationCategory.ENVIRONMENT.title,
            problem = "Tap water in municipal facilities and rural wells contains invisible microplastic particles down to 10 microns, yet laboratory spectrographic testing costs hundreds of dollars per sample.",
            solution = "A portable flow-through optical laser diffraction chamber with photo-diode array that identifies microparticle density and filters particles through a magnetic bio-char sleeve.",
            working = "Water passes through a 3D-printed optical flow cell. A 650nm red laser beam is refracted by passing particulates. Scattering patterns are captured by an optical receiver to compute particle suspension levels.",
            innovation = "Replaces expensive laboratory spectrometers with a $45 portable continuous monitoring device that gives immediate red/yellow/green safety readouts on an OLED display.",
            components = "Red Laser Diode 5mW 650nm, High-Speed Photodiode Sensor array, Arduino Nano ESP32, 0.96 inch I2C OLED display, Bio-char activated carbon filter cartridge, Flow-rate meter.",
            estimatedCost = "$42 USD",
            photoUrls = listOf(
                "https://images.unsplash.com/photo-1581092335397-9583fe92d232?w=800&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1532187863486-abf9dbad1b69?w=800&auto=format&fit=crop&q=80"
            ),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            documentUrl = "Water_Filtration_Spec_Sheet.pdf",
            status = ProjectStatus.PENDING_REVIEW.name,
            isFeatured = false,
            aiInnovationScore = 81,
            aiVerdict = "💡 High Practical Value (Low-Cost Optical Water Quality Analyzer)",
            aiAnalysis = "High community health potential. Leveraging red laser scattering against photodiode thresholds achieves affordable micro-particulate detection without requiring thousand-dollar laboratory bench spectrometers.",
            aiSuggestions = "• Calibrate photodiode voltage curves against certified silica microbeads to determine ppm accuracy.\n• Add a Bluetooth Low Energy module to geolocate water tests onto an open-source municipal water safety map.\n• Incorporate a self-cleaning backflush servo valve to prolong bio-char filter lifespan.",
            viewsCount = 112,
            likesCount = 19,
            createdAt = System.currentTimeMillis() - 86400000L * 2,
            updatedAt = System.currentTimeMillis() - 86400000L * 2
        ),
        Project(
            id = "proj_firefighter_drone",
            ownerId = "user_student_2",
            ownerName = "Maya Chen",
            school = "Bay Area Tech High",
            gradeClass = "Grade 12",
            cityState = "Oakland, CA",
            title = "Autonomous Firefighter Drone Scout",
            category = InnovationCategory.ROBOTICS.title,
            problem = "Structural fires risk firefighter lives when personnel must enter burning buildings without knowledge of structural beam integrity or thermal hotspots.",
            solution = "A heat-shielded micro quadcopter capable of autonomous indoor waypoint navigation that streams real-time thermal gradient maps to firefighters outside.",
            working = "Equipped with an AMG8833 8x8 IR grid thermal camera and ultrasonic rangefinders. When deployed through a window, it flies forward, maps wall boundaries, and flags areas exceeding 300°C.",
            innovation = "Uses lightweight ceramic aerogel insulation blankets and custom carbon-fiber prop guards to operate up to 220°C for 6 minutes continuously.",
            components = "Carbon fiber 3-inch drone frame, Betaflight F4 flight controller, AMG8833 Thermal Sensor, HC-SR04 ultrasonic sensors, 5.8GHz video transmitter, Aerogel insulation strip.",
            estimatedCost = "$160 USD",
            photoUrls = listOf(
                "https://images.unsplash.com/photo-1527977966376-1c8408f9f108?w=800&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1508614589041-895b88991e3e?w=800&auto=format&fit=crop&q=80"
            ),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            documentUrl = "Firefighter_Drone_Thermal_Field_Log.pdf",
            status = ProjectStatus.UNDER_REVIEW.name,
            isFeatured = false,
            reviewerId = "user_admin_1",
            reviewerName = "Dr. Elena Vance",
            privateReviewerNotes = "Currently testing aerogel thermal transfer specs with our lab sensor rig.",
            viewsCount = 280,
            likesCount = 47,
            createdAt = System.currentTimeMillis() - 86400000L * 4,
            updatedAt = System.currentTimeMillis() - 86400000L * 1
        ),
        Project(
            id = "proj_piezo_floor",
            ownerId = "user_student_1",
            ownerName = "Alex Rivera",
            school = "St. Jude STEM Academy",
            gradeClass = "Grade 11",
            cityState = "San Jose, CA",
            title = "Piezoelectric Energy Harvesting Floor Tiles",
            category = InnovationCategory.RENEWABLE_ENERGY.title,
            problem = "School corridors experience over 5,000 footsteps every single day, with all kinetic mechanical energy dissipating as waste heat and noise.",
            solution = "Modular recycled rubber tiles embedded with 12 piezoelectric discs and a bridge rectifier circuit that stores harvested micro-joules into a supercapacitor bank to power hallway emergency nightlights.",
            working = "Compressive footsteps deflect brass diaphragm piezoelectric discs, producing alternating current pulses. Full-wave diode bridges rectify pulses to DC charge stored in a 5.5V 4.0F supercapacitor.",
            innovation = "Direct mechanical spring suspension prevents disc cracking under heavy adult footsteps while amplifying deflection stroke by 40%.",
            components = "12x 35mm Brass Piezoelectric elements, 4x 1N4148 Schottky diode rectifiers, 5.5V 4.0F supercapacitor, Recycled crumb rubber interlocking tiles, 3.3V Ultra-low-power LED driver.",
            estimatedCost = "$28 USD",
            photoUrls = listOf(
                "https://images.unsplash.com/photo-1509391365360-2e959784a276?w=800&auto=format&fit=crop&q=80"
            ),
            videoUrl = "",
            documentUrl = "Piezo_Energy_Graphs.pdf",
            status = ProjectStatus.CHANGES_REQUESTED.name,
            isFeatured = false,
            reviewerId = "user_admin_1",
            reviewerName = "Dr. Elena Vance",
            reviewerFeedback = "Brilliant concept! However, please provide circuit schematics for the capacitor rectifier bridge and battery efficiency graphs during continuous 100-step test cycles before final publishing approval.",
            privateReviewerNotes = "Promising work, need to ensure the rectifier doesn't overheat under continuous load.",
            viewsCount = 189,
            likesCount = 33,
            createdAt = System.currentTimeMillis() - 86400000L * 9,
            updatedAt = System.currentTimeMillis() - 86400000L * 3
        ),
        Project(
            id = "proj_bionic_hand",
            ownerId = "user_student_2",
            ownerName = "Maya Chen",
            school = "Bay Area Tech High",
            gradeClass = "Grade 12",
            cityState = "Oakland, CA",
            title = "Bionic Prosthetic Hand with Haptic Feedback",
            category = InnovationCategory.HEALTHCARE.title,
            problem = "Commercial myoelectric prosthetic hands cost between $15,000 and $40,000, making them completely inaccessible to growing children and low-income families.",
            solution = "A 3D-printed anthropomorphic tendon-driven prosthetic hand controlled by surface electromyography (sEMG) arm sensors and miniature force-sensitive resistors that pulse haptic motors when gripping delicate items.",
            working = "MyoWare sEMG sensors detect forearm muscle contractions. An onboard Teensy 4.0 microcontroller processes raw voltage spikes and actuates 5 micro-metal geared motors to curl nylon tendon cables.",
            innovation = "Closed-loop tactile sensory feedback allows the user to hold eggs and plastic cups without crushing them even when eyes are closed.",
            components = "PLA+ 3D Printed Hand Chassis, 5x Micro Metal Gearmotors 100:1, MyoWare 2.0 EMG muscle sensor, 5x FSR402 force sensors, Teensy 4.0, ERM haptic vibrator.",
            estimatedCost = "$95 USD",
            photoUrls = listOf(
                "https://images.unsplash.com/photo-1531403009284-440f080d1e12?w=800&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=800&auto=format&fit=crop&q=80"
            ),
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
            documentUrl = "Bionic_Prosthetic_Design_Report.pdf",
            status = ProjectStatus.APPROVED.name,
            isFeatured = true,
            reviewerId = "user_admin_1",
            reviewerName = "Dr. Elena Vance",
            reviewerFeedback = "Superb mechanical design and thoughtful tactile sensory loop. Approved for national showcase inclusion!",
            badgeAwarded = InnovationBadge.YOUNG_INNOVATOR.title,
            viewsCount = 680,
            likesCount = 112,
            createdAt = System.currentTimeMillis() - 86400000L * 18,
            updatedAt = System.currentTimeMillis() - 86400000L * 6,
            publishedAt = System.currentTimeMillis() - 86400000L * 6
        ),
        Project(
            id = "proj_pill_dispenser",
            ownerId = "user_student_1",
            ownerName = "Alex Rivera",
            school = "St. Jude STEM Academy",
            gradeClass = "Grade 11",
            cityState = "San Jose, CA",
            title = "Smart IoT Pill Dispenser with Audio Reminder",
            category = InnovationCategory.SMART_DEVICES.title,
            problem = "Elderly patients with multiple prescriptions frequently miss dosages or double-dose by mistake.",
            solution = "A rotating 7-day pill carousel that unlocks only the scheduled dosage compartment at specific times and sends alerts to family members if pills are not retrieved within 20 minutes.",
            working = "Stepper motor positions the selected compartment under an optical trapdoor. A DFPlayer mini plays friendly voice reminders. An IR beam sensor verifies pill drop into the cup.",
            innovation = "Simple tactile one-button operation with fail-safe anti-tamper lock designed for dementia patients.",
            components = "NodeMCU ESP8266, 28BYJ-48 Stepper Motor + ULN2003, DFPlayer Mini MP3, 2W speaker, IR obstacle sensor, Food-grade 3D printed carousel.",
            estimatedCost = "$35 USD",
            photoUrls = listOf(
                "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=800&auto=format&fit=crop&q=80"
            ),
            videoUrl = "",
            documentUrl = "",
            status = ProjectStatus.DRAFT.name,
            isFeatured = false,
            viewsCount = 4,
            likesCount = 0,
            createdAt = System.currentTimeMillis() - 86400000L * 1,
            updatedAt = System.currentTimeMillis() - 86400000L * 1
        )
    )

    val sampleNotifications = listOf(
        NotificationItem(
            id = "notif_1",
            userId = "user_student_1",
            projectId = "proj_water_filtration",
            projectTitle = "Low-Cost Microplastic Water Filtration Sensor",
            type = NotificationType.SUBMISSION_RECEIVED.name,
            title = "Submission Received",
            message = "Your project 'Low-Cost Microplastic Water Filtration Sensor' has been queued for review by the InnovateX Review Board.",
            isRead = false,
            createdAt = System.currentTimeMillis() - 86400000L * 2
        ),
        NotificationItem(
            id = "notif_2",
            userId = "user_student_1",
            projectId = "proj_piezo_floor",
            projectTitle = "Piezoelectric Energy Harvesting Floor Tiles",
            type = NotificationType.CHANGES_REQUESTED.name,
            title = "Changes Requested by Reviewer",
            message = "Dr. Elena Vance requested improvements on your submission: 'Please provide circuit schematics for the capacitor rectifier bridge and battery efficiency graphs.'",
            isRead = false,
            createdAt = System.currentTimeMillis() - 86400000L * 3
        ),
        NotificationItem(
            id = "notif_3",
            userId = "user_student_1",
            projectId = "proj_hydroponics",
            projectTitle = "Solar-Powered Automated Hydroponics Farm",
            type = NotificationType.BADGE_RECEIVED.name,
            title = "Green Innovation Badge Awarded!",
            message = "Congratulations! Your project received the official 'Green Innovation' distinction for environmental excellence.",
            isRead = true,
            createdAt = System.currentTimeMillis() - 86400000L * 5
        ),
        NotificationItem(
            id = "notif_4",
            userId = "user_student_1",
            projectId = "proj_sensory_vest",
            projectTitle = "AI Blind Navigation Sensory Vest",
            type = NotificationType.PROJECT_APPROVED.name,
            title = "Project Approved & Published",
            message = "Your project 'AI Blind Navigation Sensory Vest' has been officially approved and published to the public explore feed.",
            isRead = true,
            createdAt = System.currentTimeMillis() - 86400000L * 8
        )
    )

    val sampleReviews = listOf(
        Review(
            id = "rev_1",
            projectId = "proj_hydroponics",
            reviewerId = "user_admin_1",
            reviewerName = "Dr. Elena Vance",
            action = "APPROVED",
            studentFeedback = "Exceptional engineering rigour! The closed-loop irrigation system and power budget were verified successfully with healthy kale and lettuce yields over 4 weeks.",
            privateNotes = "Top candidate for the State Science Fair grant. Solar power calculations are sound.",
            badgeAwarded = InnovationBadge.GREEN_INNOVATION.title,
            rating = 5,
            reviewerRole = "Chief Science Reviewer",
            constructiveTip = "Consider adding automated SMS alerts via SIM800L module when pH drifts out of tolerance for remote monitoring.",
            timestamp = System.currentTimeMillis() - 86400000L * 5
        ),
        Review(
            id = "rev_peer_1",
            projectId = "proj_arduino_obstacle_robot",
            reviewerId = "user_student_2",
            reviewerName = "Maya Chen",
            action = "PEER_REVIEW",
            studentFeedback = "Loved your rover prototype! The ultrasonic servo scanning algorithm is super smooth and avoids tight corner traps really effectively. Tested code on my own 2WD chassis.",
            privateNotes = "",
            badgeAwarded = null,
            rating = 5,
            reviewerRole = "Student Innovator",
            constructiveTip = "You could add small rubber silicone bands to the plastic TT gear wheels for better traction on polished laboratory tiles.",
            timestamp = System.currentTimeMillis() - 86400000L * 2
        ),
        Review(
            id = "rev_peer_2",
            projectId = "proj_arduino_obstacle_robot",
            reviewerId = "user_student_3",
            reviewerName = "Zain Malik",
            action = "PEER_REVIEW",
            studentFeedback = "Very clean breadboard wiring and great use of dual power supply isolation. Solved the common microcontroller brownout reset problem nicely!",
            privateNotes = "",
            badgeAwarded = null,
            rating = 4,
            reviewerRole = "Robotics Club Member",
            constructiveTip = "A small buzzer beep right before turning would make it safer in crowded school hallway demonstrations.",
            timestamp = System.currentTimeMillis() - 86400000L * 1
        ),
        Review(
            id = "rev_peer_3",
            projectId = "proj_hydroponics",
            reviewerId = "user_student_2",
            reviewerName = "Maya Chen",
            action = "PEER_REVIEW",
            studentFeedback = "Incredible zero-grid agriculture build! The 3D printed pods distribute water evenly without clogging or root rot.",
            privateNotes = "",
            badgeAwarded = null,
            rating = 5,
            reviewerRole = "Biotech Student",
            constructiveTip = "Using dark-tinted PETG instead of standard white PLA will prevent algae accumulation inside the nutrient lines.",
            timestamp = System.currentTimeMillis() - 86400000L * 3
        ),
        Review(
            id = "rev_2",
            projectId = "proj_piezo_floor",
            reviewerId = "user_admin_1",
            reviewerName = "Dr. Elena Vance",
            action = "CHANGES_REQUESTED",
            studentFeedback = "Brilliant concept! However, please provide circuit schematics for the capacitor rectifier bridge and battery efficiency graphs during continuous 100-step test cycles before final publishing approval.",
            privateNotes = "The student has great intuition. Needs more data on power dissipation.",
            badgeAwarded = null,
            rating = 4,
            reviewerRole = "Chief Science Reviewer",
            constructiveTip = "Include Schottky diodes in the rectifier bridge to minimize forward voltage drop across piezoelectric pulses.",
            timestamp = System.currentTimeMillis() - 86400000L * 3
        )
    )

    val sampleClasses = listOf(
        LiveClassSession(
            id = "class_1",
            title = "Robotics & Microcontroller Masterclass",
            instructorName = "Prof. Rahul Verma",
            subject = "Robotics & Hardware",
            dateTimeText = "Today, 5:00 PM - Live Zoom Class",
            zoomMeetingId = "842 9102 5521",
            zoomPassword = "innovate2026",
            zoomLink = "https://zoom.us/j/84291025521?pwd=innovate2026",
            description = "Learn real-world sensor integration, servo motor control, and autonomous robot navigation using Arduino Uno and ESP32 microcontrollers.",
            priceText = "250 PKR / 10 Days Access",
            isLiveNow = true,
            durationMinutes = 60,
            scheduledTimestamp = System.currentTimeMillis() - 15 * 60 * 1000L
        ),
        LiveClassSession(
            id = "class_2",
            title = "AI & Vision Models for Science Fairs",
            instructorName = "Dr. Elena Vance",
            subject = "Artificial Intelligence",
            dateTimeText = "Today in 45 mins - Zoom Live",
            zoomMeetingId = "915 3301 8842",
            zoomPassword = "sciencepass",
            zoomLink = "https://zoom.us/j/91533018842?pwd=sciencepass",
            description = "Step-by-step interactive session to train custom TensorFlow Lite computer vision models on Raspberry Pi for smart science prototypes.",
            priceText = "250 PKR / 10 Days Access",
            isLiveNow = false,
            durationMinutes = 90,
            scheduledTimestamp = System.currentTimeMillis() + 45 * 60 * 1000L
        ),
        LiveClassSession(
            id = "class_3",
            title = "3D CAD Prototyping Workshop",
            instructorName = "Engr. Maya Chen",
            subject = "CAD & 3D Printing",
            dateTimeText = "Tomorrow, 4:00 PM - Zoom Session",
            zoomMeetingId = "732 1198 4409",
            zoomPassword = "cad2026",
            zoomLink = "https://zoom.us/j/73211984409?pwd=cad2026",
            description = "Design custom enclosures, mechanical gears, and 3D printable prototype chassis using Fusion 360 and Tinkercad.",
            priceText = "250 PKR / 10 Days Access",
            isLiveNow = false,
            durationMinutes = 75,
            scheduledTimestamp = System.currentTimeMillis() + 26 * 60 * 60 * 1000L
        )
    )

    suspend fun seedDatabase(database: AppDatabase) {
        database.userDao().insertUsers(sampleUsers)
        database.projectDao().insertProjects(sampleProjects)
        database.notificationDao().insertNotifications(sampleNotifications)
        for (r in sampleReviews) {
            database.reviewDao().insertReview(r)
        }
        database.liveClassDao().insertClasses(sampleClasses)
    }
}
