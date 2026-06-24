import SwiftUI
import UserNotifications
import Shared

final class NotificationDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    private let reminderCategory = "DRINK_REMINDER"
    private let openAddDrinkAction = "OPEN_ADD_DRINK"

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil
    ) -> Bool {
        let center = UNUserNotificationCenter.current()
        center.delegate = self

        let addDrink = UNNotificationAction(
            identifier: openAddDrinkAction,
            title: "Add drink",
            options: [.foreground]
        )

        let category = UNNotificationCategory(
            identifier: reminderCategory,
            actions: [addDrink],
            intentIdentifiers: [],
            options: []
        )

        center.setNotificationCategories([category])

        return true
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        if response.actionIdentifier == openAddDrinkAction ||
               response.actionIdentifier == UNNotificationDefaultActionIdentifier {
            center.removeDeliveredNotifications(
                withIdentifiers: [response.notification.request.identifier]
            )
            AppActionStore.shared.openAddDrinkDialogFromPlatform()
        }

        completionHandler()
    }
}
