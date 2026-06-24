import SwiftUI
import UserNotifications

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(NotificationDelegate.self)
    var notificationDelegate

    init() {
        requestNotificationPermissionOnFirstLaunch()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }

    private func requestNotificationPermissionOnFirstLaunch() {
        let center = UNUserNotificationCenter.current()

        center.requestAuthorization(options: [.alert, .sound]) { granted, error in
            if let error {
                print("Notification permission error: \(error.localizedDescription)")
                return
            }

            print("Notification permission granted: \(granted)")
        }
    }
}
