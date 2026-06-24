import UserNotifications

final class IOSWaterReminderScheduler {
    func scheduleDrinkReminder(intervalSeconds: TimeInterval, identifier: String = "drink-water-reminder") {
        let content = UNMutableNotificationContent()
        content.title = "Time to drink water"
        content.body = "Add your next drink"
        content.sound = .default
        content.categoryIdentifier = "DRINK_REMINDER"

        let trigger = UNTimeIntervalNotificationTrigger(
            timeInterval: intervalSeconds,
            repeats: false
        )

        let request = UNNotificationRequest(
            identifier: identifier,
            content: content,
            trigger: trigger
        )

        UNUserNotificationCenter.current().add(request) { error in
            if let error {
                print("Failed to schedule reminder: \(error.localizedDescription)")
            }
        }
    }

    func cancel() {
        UNUserNotificationCenter.current()
            .removePendingNotificationRequests(withIdentifiers: ["drink-water-reminder"])
    }
}
