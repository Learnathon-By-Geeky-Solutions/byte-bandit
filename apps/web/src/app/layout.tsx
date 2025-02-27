import type {Metadata} from "next";
import "./globals.css";
import {ThemeProvider} from "next-themes";
import {dm_Sans} from "@/app/fonts";


export const metadata: Metadata = {
    title: "Oakcan",
    description: "Store, access, and share files with speed",
};

/**
 * Renders the application's root layout with global styles and theming support.
 *
 * This layout sets the HTML language to English and suppresses hydration warnings. It applies a custom font,
 * flex styling to center the content, and wraps the child components within a ThemeProvider for class-based theming.
 * The content is constrained to a maximum width to ensure consistent layout.
 *
 * @param children - The child components to be rendered within the layout.
 * @returns The complete HTML structure with a themed body and centered content.
 */
export default function RootLayout({
                                       children,
                                   }: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <html lang="en" suppressHydrationWarning={true}>
        <body
            className={`${dm_Sans.className} antialiased flex flex-col justify-center items-center`}
        >
        <ThemeProvider attribute={"class"} defaultTheme={"system"} enableSystem={true} disableTransitionOnChange={true}>
            <div className={"max-w-[900px]"}>
                {children}
            </div>
        </ThemeProvider>
        </body>
        </html>
    );
}
