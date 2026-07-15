package id.my.agungdh.dawudtracker.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.agungdh.dawudtracker.data.entity.FastingStatus
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val monthDays by viewModel.monthDays.collectAsState()
    val daysInMonth by viewModel.daysInMonth.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val totalFasted by viewModel.totalFasted.collectAsState(0)
    val monthlyFasted by viewModel.monthlyFasted.collectAsState(0)
    val longestStreak by viewModel.longestStreak.collectAsState(0)
    val notificationTime by viewModel.notificationTime.collectAsState()

    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("id-ID"))
    monthFormat.timeZone = TimeZone.getTimeZone("UTC")

    val dayHeaders = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Stats cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    label = "Total",
                    value = totalFasted.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Bulan Ini",
                    value = monthlyFasted.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Streak",
                    value = "$longestStreak hari",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Month navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { viewModel.navigateMonth(false) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Bulan sebelumnya")
                }
                Text(
                    text = monthFormat.format(currentMonth.time),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { viewModel.navigateMonth(true) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Bulan berikutnya")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Day headers
            Row(modifier = Modifier.fillMaxWidth()) {
                dayHeaders.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Calendar grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(monthDays) { day ->
                    if (day == 0) {
                        Box(modifier = Modifier.aspectRatio(1f))
                    } else {
                        val dateCal = currentMonth.clone() as Calendar
                        dateCal.set(Calendar.DAY_OF_MONTH, day)
                        dateCal.set(Calendar.HOUR_OF_DAY, 0)
                        dateCal.set(Calendar.MINUTE, 0)
                        dateCal.set(Calendar.SECOND, 0)
                        dateCal.set(Calendar.MILLISECOND, 0)
                        val dateMs = dateCal.timeInMillis
                        val status = daysInMonth[dateMs]

                        val todayCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                        todayCal.set(Calendar.HOUR_OF_DAY, 0)
                        todayCal.set(Calendar.MINUTE, 0)
                        todayCal.set(Calendar.SECOND, 0)
                        todayCal.set(Calendar.MILLISECOND, 0)
                        val isToday = dateMs == todayCal.timeInMillis

                        val bgColor = when (status) {
                            FastingStatus.FASTED -> Color(0xFF4CAF50).copy(alpha = 0.3f)
                            FastingStatus.NOT_FASTED -> Color(0xFFF44336).copy(alpha = 0.3f)
                            null -> Color.Transparent
                        }
                        val borderColor = if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent

                        val year = currentMonth.get(Calendar.YEAR)
                        val month = currentMonth.get(Calendar.MONTH)

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(bgColor)
                                .then(
                                    if (isToday) Modifier.border(
                                        2.dp,
                                        borderColor,
                                        RoundedCornerShape(8.dp)
                                    ) else Modifier
                                )
                                .clickable { viewModel.toggleDay(year, month, day) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                color = when (status) {
                                    FastingStatus.FASTED -> Color(0xFF2E7D32)
                                    FastingStatus.NOT_FASTED -> Color(0xFFC62828)
                                    null -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = Color(0xFF4CAF50), label = "Puasa")
                Spacer(modifier = Modifier.width(16.dp))
                LegendItem(color = Color(0xFFF44336), label = "Tidak")
                Spacer(modifier = Modifier.width(16.dp))
                LegendItem(color = Color.Transparent, label = "Belum")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mark today buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.markToday(FastingStatus.FASTED) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hari ini Puasa")
                }
                Button(
                    onClick = { viewModel.markToday(FastingStatus.NOT_FASTED) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    )
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tidak Puasa")
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(if (color == Color.Transparent) Color.Gray.copy(alpha = 0.3f) else color)
                .then(
                    if (color == Color.Transparent) Modifier.border(
                        1.dp,
                        Color.Gray.copy(alpha = 0.5f),
                        CircleShape
                    ) else Modifier
                )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}
