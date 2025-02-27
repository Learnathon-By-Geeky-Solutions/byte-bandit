"use client"

import * as React from "react"
import { ThemeProvider as NextThemesProvider } from "next-themes"

/**
 * Wraps NextThemesProvider to apply theming settings to its children.
 *
 * This component forwards all received props and the rendered children to the NextThemesProvider
 * from the next-themes library, ensuring that theme-related context is available to descendant components.
 *
 * @returns The rendered NextThemesProvider component with the provided theming configuration.
 */
export function ThemeProvider({
                                  children,
                                  ...props
                              }: React.ComponentProps<typeof NextThemesProvider>) {
    return <NextThemesProvider {...props}>{children}</NextThemesProvider>
}
