import SwiftUI
import UserNotifications
import Shared

final class NotificationDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    private let reminderCategory = ReminderNotificationSpec.shared.IOS_CATEGORY_IDENTIFIER
    private let openAddDrinkAction = ReminderNotificationSpec.shared.IOS_OPEN_ADD_DRINK_ACTION

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
