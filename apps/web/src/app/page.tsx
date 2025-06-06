"use client";
import {Button} from "@/components/ui/button";
import NavigationBar from "@/app/components/navigation-bar";
import {motion} from "framer-motion";
import {TEXTS} from "@/lib/constants";
import {MoveRight} from "lucide-react";
import Link from "next/link";

export default function Home() {
    return (
        <div className="h-screen w-full flex flex-col items-center bg-background">
            <NavigationBar/>
            <motion.section
                className={'flex flex-col text-center justify-center items-center gap-4 w-full md:w-[90vw] lg:w-[1000px] h-[75vh]'}
                initial={{opacity: 0, y: 20, filter: 'blur(10px)'}}
                animate={{opacity: 1, y: 0, filter: 'blur(0px)'}}
                transition={{duration: 0.5, ease: "easeInOut"}}
            >
                <motion.section
                    className={"flex flex-col gap-2"}
                    initial={{opacity: 0, y: 20, filter: 'blur(10px)'}}
                    animate={{opacity: 1, y: 0, filter: 'blur(0px)'}}
                    transition={{duration: 0.5, ease: "easeInOut", delay: 0.3}}
                >
                    <h1 className={"text-4xl md:text-5xl font-black"}>{TEXTS.LANDING_PAGE.MAIN_HEADING}</h1>
                    <h5 className={"text-foreground/55"}>{TEXTS.LANDING_PAGE.SUB_HEADING}</h5>
                </motion.section>
                <Link href={"/login"}>
                    <motion.div whileHover={"hover"} className={"flex fle-row  items-center"}>
                        <Button className="w-min">
                            {TEXTS.LANDING_PAGE.CTA_TEXT}
                            <motion.div
                                variants={{
                                    hover: {x: 5, transition: {duration: 0.2}}
                                }}
                                className="ml-2"
                            >
                                <MoveRight/>
                            </motion.div>
                        </Button>
                    </motion.div>
                </Link>
            </motion.section>
        </div>
    );
}
