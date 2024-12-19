package com.example.hiitintervaltimer.ui.commons

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hiitintervaltimer.ui.data.CountedInterval
import com.example.hiitintervaltimer.ui.data.IntervalModel
import com.example.hiitintervaltimer.ui.data.TimedInterval

@Composable
fun IntervalList(intervals: List<IntervalModel>) {
    intervals.forEachIndexed { index, interval ->
        when (interval) {
            is TimedInterval -> IntervalCard(interval)
            is CountedInterval -> IntervalCard(interval)
        }
    }
}

@Composable
fun IntervalCard(interval: TimedInterval) {
    Card(modifier = Modifier.padding(4.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = interval.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = interval.desc,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Value: ${interval.value / 60}:${interval.value % 60} minutes",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Delay: ${interval.delay} seconds",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun IntervalCard(interval: CountedInterval) {
    Card(modifier = Modifier.padding(4.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = interval.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = interval.desc,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Value: ${interval.value}x",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Delay: ${interval.delay} seconds",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}