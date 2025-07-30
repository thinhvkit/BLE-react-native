package com.myprotect.projectx.permissions

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import myprotect_mobile.shared.generated.resources.Res
import myprotect_mobile.shared.generated.resources.app_permissions_icon_appear_on_top
import myprotect_mobile.shared.generated.resources.app_permissions_icon_bluetooth
import myprotect_mobile.shared.generated.resources.app_permissions_icon_ignore_battery
import myprotect_mobile.shared.generated.resources.app_permissions_icon_location
import myprotect_mobile.shared.generated.resources.app_permissions_icon_phone
import myprotect_mobile.shared.generated.resources.appear_on_top_type
import myprotect_mobile.shared.generated.resources.appear_on_top_type_desc
import myprotect_mobile.shared.generated.resources.battery_type
import myprotect_mobile.shared.generated.resources.battery_type_desc
import myprotect_mobile.shared.generated.resources.bell
import myprotect_mobile.shared.generated.resources.bluetooth_type
import myprotect_mobile.shared.generated.resources.bluetooth_type_desc
import myprotect_mobile.shared.generated.resources.location_type
import myprotect_mobile.shared.generated.resources.location_type_desc
import myprotect_mobile.shared.generated.resources.notification_type
import myprotect_mobile.shared.generated.resources.notification_type_desc
import myprotect_mobile.shared.generated.resources.phone_type
import myprotect_mobile.shared.generated.resources.phone_type_desc

enum class PermissionType {
    PHONE {
        override val label: StringResource
            get() = Res.string.phone_type
        override val description: StringResource
            get() = Res.string.phone_type_desc
        override val icon: DrawableResource
            get() = Res.drawable.app_permissions_icon_phone

    },
    LOCATION {
        override val label: StringResource
            get() = Res.string.location_type
        override val description: StringResource
            get() = Res.string.location_type_desc
        override val icon: DrawableResource
            get() = Res.drawable.app_permissions_icon_location
    },
    NOTIFICATION {
        override val label: StringResource
            get() = Res.string.notification_type
        override val description: StringResource
            get() = Res.string.notification_type_desc
        override val icon: DrawableResource
            get() = Res.drawable.bell
    },
    APPEAR_ON_TOP {
        override val label: StringResource
            get() =  Res.string.appear_on_top_type
        override val description: StringResource
            get() = Res.string.appear_on_top_type_desc
        override val icon: DrawableResource
            get() = Res.drawable.app_permissions_icon_appear_on_top
    },
    BATTERY {
        override val label: StringResource
            get() = Res.string.battery_type
        override val description: StringResource
            get() = Res.string.battery_type_desc
        override val icon: DrawableResource
            get() = Res.drawable.app_permissions_icon_ignore_battery
    },
    BLUETOOTH {
        override val label: StringResource
            get() = Res.string.bluetooth_type
        override val description: StringResource
            get() = Res.string.bluetooth_type_desc
        override val icon: DrawableResource
            get() = Res.drawable.app_permissions_icon_bluetooth
    };

    abstract val label: StringResource

    abstract val description: StringResource

    abstract val icon: DrawableResource
}
