"use client";
import {ModeToggle} from "@/components/theme-toggler";
import {Button} from "@/components/ui/button";
import NavigationBar from "@/app/components/navigation-bar";
import {motion} from "framer-motion";
import {TEXTS} from "@/lib/constants";
import {MoveRight} from "lucide-react";
import Link from "next/link";

/**
 * Renders the application's landing page.
 *
 * This component displays a full-screen layout featuring:
 * - A navigation bar at the top.
 * - An animated section with main and subheadings sourced from a centralized text configuration.
 * - A call-to-action button that navigates to the login page, enhanced with hover animations.
 * - A mode toggle for switching UI themes, visible on larger screens.
 *
 * Animations are implemented using framer-motion, creating subtle fade-in and slide-up effects with a blur transition.
 */
export default function Home() {
    return (
        <div className="h-screen w-full">
            <NavigationBar/>
            <motion.section
                className={'flex flex-col text-center justify-center items-center gap-4 w-full h-[75vh]'}
                initial={{opacity: 0, y: 20, filter: 'blur(10px)'}}
                animate={{opacity: 1, y: 0, filter: 'blur(0px)'}}
                transition={{duration: 0.5, ease: "easeInOut"}}
            >
                <motion.section
                    className={"flex flex-col gap-2"}
                    initial={{opacity: 0, y: 20, filter: 'blur(10px)'}}
                    animate={{opacity: 1, y: 0, filter: 'blur(0px'}}
                    transition={{duration: 0.5, ease: "easeInOut", delay: 0.3}}
                >
                    <h1 className={"text-4xl md:text-5xl font-black"}>{TEXTS.LANDING_PAGE.MAIN_HEADING}</h1>
                    <h5 className={"text-foreground/55"}>{TEXTS.LANDING_PAGE.SUB_HEADING}</h5>
                </motion.section>
                <Link href={"/login"}>
                    <Button className="w-min">
                        <motion.div whileHover={"hover"} className={"flex fle-row  items-center"}>
                            {TEXTS.LANDING_PAGE.CTA_TEXT}
                            <motion.div
                                variants={{
                                    hover: {x: 5, transition: {duration: 0.2}}
                                }}
                                className="ml-2"
                            >
                                <MoveRight/>
                            </motion.div>
                        </motion.div>
                    </Button>
                </Link>
            </motion.section>
            <ModeToggle className={"hidden sm:flex absolute right-0 top-0 m-2"}/>
        </div>
    );
}
